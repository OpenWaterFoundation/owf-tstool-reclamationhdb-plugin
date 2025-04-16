// ReclamationHDBDataStore - Data store for Reclamation HDB database.

/* NoticeStart

CDSS Time Series Processor Java Library
CDSS Time Series Processor Java Library is a part of Colorado's Decision Support Systems (CDSS)
Copyright (C) 1994-2025 Colorado Department of Natural Resources

CDSS Time Series Processor Java Library is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

CDSS Time Series Processor Java Library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

You should have received a copy of the GNU General Public License
    along with CDSS Time Series Processor Java Library.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

package org.openwaterfoundation.tstool.plugin.reclamationhdb.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openwaterfoundation.tstool.plugin.reclamationhdb.dao.ReclamationHDB_SiteTimeSeriesMetadata;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.ui.ReclamationHDB_TimeSeries_CellRenderer;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.ui.ReclamationHDB_TimeSeries_InputFilter_JPanel;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.ui.ReclamationHDB_TimeSeries_TableModel;

import RTi.DMI.AbstractDatabaseDataStore;
import RTi.DMI.DMI;
import RTi.TS.TS;
import RTi.TS.TSIdent;
import RTi.Util.GUI.InputFilter_JPanel;
import RTi.Util.GUI.JWorksheet_AbstractExcelCellRenderer;
import RTi.Util.GUI.JWorksheet_AbstractRowTableModel;
import RTi.Util.Message.Message;
import RTi.Util.Time.DateTime;
import riverside.datastore.PluginDataStore;

/**
Data store for Reclamation HDB database.
This class maintains the database connection information in a general way.
*/
public class ReclamationHDBDataStore extends AbstractDatabaseDataStore implements PluginDataStore
{

	// The following are defined in TSToolConstants but put here since don't want that dependency.
	//String TIMESTEP_AUTO = "Auto";
	//String TIMESTEP_MINUTE = "Minute";
	private final String TIMESTEP_HOUR = "Hour";
	private final String TIMESTEP_DAY = "Day";
	private final String TIMESTEP_MONTH = "Month";
	private final String TIMESTEP_YEAR = "Year";
	private final String TIMESTEP_IRREGULAR = "Irregular"; // Use for real-time where interval is not known.

	/**
	 * Properties for the plugin, used to help with application integration.
	 */
	Map<String,Object> pluginProperties = new LinkedHashMap<>();

	/**
	Construct a data store given a DMI instance, which is assumed to be open.
	@param name identifier for the data store
	@param description name for the data store
	@param dmi DMI instance to use for the data store.
	*/
	public ReclamationHDBDataStore ( String name, String description, DMI dmi ) {
    	setName ( name );
    	setDescription ( description );
    	setDMI ( dmi );
	}

	/**
	Check the database connection.  Sometimes the connection gets dropped due to timeout from inaction.
	A simple, fast query is run and if it fails the connection is re-established.
	It is assumed that the DMI instance has been populated with data that can be used for the connection.
	Although this method could be called near low-level database statement calls (for example in DMI read/write methods),
	for performance it is probably best to call at a higher level before a group of database statements are executed.
	@return true if the connection could be established, false if not.
	*/
	public boolean checkDatabaseConnection () {
		int retries = 5;
		for ( int i = 0; i < retries; i++ ) {
			DMI dmi = getDMI();
			try {
				if ( dmi == null ) {
					// Datastore was never initialized properly when software started:
					// - this is a bigger problem than can be fixed here.
					// - can't set status on DMI since null instance
					return false;
				}
				else {
					ReclamationHDB_DMI rdmi = (ReclamationHDB_DMI)dmi;
					// Will throw an exception if there is an error.
					rdmi.dmiSelect("SELECT * from HDB_STATE");
					// If here the connection is in place and the query was successful.
					return true;
				}
			}
			catch ( Exception e ) {
				// Error running query so try to open the DMI.
				Message.printWarning(3, "", e);
				try {
					dmi.open();
					// If no exception it was successful, but make sure query is OK so go to the top of the loop again.
					setStatus(0);
					DateTime now = new DateTime(DateTime.DATE_CURRENT);
					setStatusMessage("Database connection automatically reopened at " + now );
					continue;
				}
				catch ( Exception e2 ) {
					// Failed to open - try again until max retries is over.
					Message.printWarning(3, "", e);
					continue;
				}
			}
		}
		// Could not establish the connection even with retries.
		setStatusMessage("Unable to open database connection after " + retries + " retries.");
		return false;
	}

	/**
	 * Create a time series input filter, used to initialize user interfaces.
	 */
	public InputFilter_JPanel createTimeSeriesListInputFilterPanel () {
		ReclamationHDB_TimeSeries_InputFilter_JPanel ifp = new ReclamationHDB_TimeSeries_InputFilter_JPanel(this, 4);
		return ifp;
	}

	/**
	 * Create a time series list table model given the desired data type, time step (interval), and input filter.
	 * The datastore performs a suitable query and creates objects to manage in the time series list.
	 * @param dataType time series data type to query, controlled by the datastore
	 * @param timeStep time interval to query, controlled by the datastore
	 * @param ifp input filter panel that provides additional filter options
	 * @return a TableModel containing the defined columns and rows.
	 */
	@SuppressWarnings("rawtypes")
	public JWorksheet_AbstractRowTableModel createTimeSeriesListTableModel(String dataType, String timeStep, InputFilter_JPanel ifp ) {
		// First query the database for the specified input.
		List<ReclamationHDB_SiteTimeSeriesMetadata> tsmetaList = readTimeSeriesMeta ( dataType, timeStep, ifp );
		return getTimeSeriesListTableModel(tsmetaList);
	}

	/**
 	* Get the properties for the plugin.
 	* A copy of the properties map is returned so that calling code cannot change the properties for the plugin.
 	* @return plugin properties map.
 	*/
	public Map<String,Object> getPluginProperties () {
		Map<String,Object> pluginProperties = new LinkedHashMap<>();
		// For now the properties are all strings so it is easy to copy.
    	for (Map.Entry<String, Object> entry : this.pluginProperties.entrySet()) {
        	pluginProperties.put(entry.getKey(), entry.getValue());
    	}
		return pluginProperties;
	}

	/**
	 * This version is required by TSTool UI.
	 * Return the list of time series data interval strings.
	 * Interval strings match TSTool conventions such as NewTimeSeries command, which uses "1Hour" rather than "1hour".
	 * This should result from calls like:  TimeInterval.getName(TimeInterval.HOUR, 0)
	 * @param dataType data type string to filter the list of data intervals.
	 * If null, blank, or "*" the data type is not considered when determining the list of data intervals.
	 */
	public List<String> getTimeSeriesDataIntervalStrings ( String dataType ) {
		// Include wildcards as per legacy behavior.
		boolean includeWildcards = true;
		return getTimeSeriesDataIntervalStrings(dataType, includeWildcards);
	}

	/**
	 * This version is required by TSTool UI.
	 * Return the list of time series data interval strings.
	 * Interval strings match TSTool conventions such as NewTimeSeries command, which uses "1Hour" rather than "1hour".
	 * This should result from calls like:  TimeInterval.getName(TimeInterval.HOUR, 0)
	 * @param dataType data type string to filter the list of data intervals.
	 * If null, blank, or "*" the data type is not considered when determining the list of data intervals.
	 * @includeWildcards if true, include "*" wildcard.
	 */
	public List<String> getTimeSeriesDataIntervalStrings ( String dataType, boolean includeWildcards ) {
		String routine = getClass().getSimpleName() + ".getTimeSeriesDataIntervalStrings";
		Message.printStatus(2, routine, "Getting interval strings for data type \"" + dataType + "\"");

		// Get the list of timesteps that are valid for the data type.
		// TODO smalers 2025-04-15 what does this mean?: Need to trigger a select to populate the input filters.
		List<String> dataIntervals = new ArrayList<>();
		dataIntervals.add ( this.TIMESTEP_HOUR );
		dataIntervals.add ( "2" + this.TIMESTEP_HOUR );
		dataIntervals.add ( "3" + this.TIMESTEP_HOUR );
		dataIntervals.add ( "4" + this.TIMESTEP_HOUR );
		dataIntervals.add ( "6" + this.TIMESTEP_HOUR );
		dataIntervals.add ( "12" + this.TIMESTEP_HOUR );
		dataIntervals.add ( "24" + this.TIMESTEP_HOUR );
		dataIntervals.add ( this.TIMESTEP_DAY );
		dataIntervals.add ( this.TIMESTEP_MONTH );
		dataIntervals.add ( this.TIMESTEP_YEAR );
		dataIntervals.add ( this.TIMESTEP_IRREGULAR ); // Instantaneous handled as irregular.

		// No need to sort the intervals since in the correct order above:
		// - if read from the database, might need to sort
		//Collections.sort(dataIntervals,String.CASE_INSENSITIVE_ORDER);

		if ( includeWildcards ) {
			// Always allow querying list of time series for all intervals:
			// - always add so that people can get a full list
			// - adding at top makes it easy to explore data without having to scroll to the end

			dataIntervals.add("*");
			if ( dataIntervals.size() > 1 ) {
				// Also add at the beginning to simplify selections:
				// - could check for a small number like 5 but there should always be a few
				dataIntervals.add(0,"*");
			}
		}

		return dataIntervals;
	}

	/**
	 * Return the list of time series data type strings.
	 * This is the version that is required by TSTool UI.
	 * These strings are the same as the dataTypes.name properties from the stationSummaries web service request.
	 * @param dataInterval data interval from TimeInterval.getName(TimeInterval.HOUR,0) to filter the list of data types.
	 * If null, blank, or "*" the interval is not considered when determining the list of data types (treat as if "*").
	 */
	public List<String> getTimeSeriesDataTypeStrings ( String dataInterval ) {
		// Legacy behavior is to include wildcards.
		boolean includeWildcards = true;
		return getTimeSeriesDataTypeStrings(dataInterval, includeWildcards );
	}

	/**
	 * Return the list of time series data type strings.
	 * These strings are the same as the parameter type list 'parametertype_name'.
	 */
	public List<String> getTimeSeriesDataTypeStrings ( String dataInterval, boolean includeWildcards ) {
		String routine = getClass().getSimpleName() + ".getTimeSeriesDataTypeStrings";

		ReclamationHDBDataStore ds = this;

		// Check the connection in case the connection timed out.
		ds.checkDatabaseConnection();
		ReclamationHDB_DMI dmi = (ReclamationHDB_DMI)ds.getDMI();
		// Get the list of valid object/data types from the database.
		List<String> dataTypes = new ArrayList<>();
		
		try {
			dataTypes = dmi.getObjectDataTypes ( true );
		}
		catch ( Exception e ) {
			Message.printWarning(3, routine, "Exception getting data types." );
			Message.printWarning(3, routine, e );
		}

		/* TODO smalers 2025-04-15 evaluate whether to do.
		boolean checkDataInterval = false;
		if ( (dataInterval != null) && !dataInterval.isEmpty() ) {
			checkDataInterval = true;
		}

		// Get the unique list of data types from the time series catalog.

		boolean dataIntervalMatched = false;
		String dataType = null;
		for ( ReclamationHDB_SiteTimeSeriesMetadata tscatalog : this.tscatalogList ) {
			if ( checkDataInterval ) {
				if ( !dataInterval.equals(tscatalog.getDataInterval()) ) {
					dataIntervalMatched = false;
				}
			}
			else {
				// Intervals re not being checked.
				dataIntervalMatched = true;
			}
			// Check whether the data type has been found before.
			boolean found = false;
			if ( dataIntervalMatched ) {
				// Data interval matched so OK to continue checking.
				dataType = tscatalog.getDataType();
				for ( String dataType2 : dataTypes ) {
					if ( dataType.equals(dataType2) ) {
						found = true;
						break;
					}
				}
			}
			if ( dataIntervalMatched && !found ) {
				// Add the data type from the TimeSeriesCatalog.
				dataTypes.add ( dataType );
			}
		}
		*/

		// Sort the names.
		Collections.sort(dataTypes, String.CASE_INSENSITIVE_ORDER);

		if ( includeWildcards ) {
			// Add wildcard at the front and end - allows querying all data types for the location:
			// - always add so that people can get a full list
			// - adding at the top makes it easy to explore data without having to scroll to the end

			dataTypes.add("*");
			if ( dataTypes.size() > 0 ) {
				dataTypes.add(0,"*");
			}
		}

		return dataTypes;
	}

	/**
 	* Return the identifier for a time series in the table model.
 	* The TSIdent parts will be uses as TSID commands.
 	* @param tableModel the table model from which to extract data
 	* @param row the displayed table row, may have been sorted
 	*/
	public TSIdent getTimeSeriesIdentifierFromTableModel( @SuppressWarnings("rawtypes") JWorksheet_AbstractRowTableModel tableModel,
		int row ) {
		//String routine = getClass().getSimpleName() + ".getTimeSeriesIdentifierFromTableModel";
    	ReclamationHDB_TimeSeries_TableModel tm = (ReclamationHDB_TimeSeries_TableModel)tableModel;

    	// Time series identifier to be returned.
    	TSIdent tsid = null;
        // The location (id), type, and time step uniquely identify the time series,
		// but the input_name is needed to indicate the database.
        ReclamationHDBDataStore ds = this;
        // Check the connection in case timeouts, etc.
        ds.checkDatabaseConnection();
        ReclamationHDB_DMI dmi = (ReclamationHDB_DMI)ds.getDMI();
        // Format the TSID using the older format that uses common names, but these are not guaranteed unique.
        String tsType = (String)tm.getValueAt( row, tm.COL_TYPE_REAL_MODEL);
        String inputName = ""; // Only used for files.
        String datastoreName = this.getName();
        if ( dmi.getTSIDStyleSDI() ) {
            // Format the TSID using the newer SDI and MRI style.
            String loc = null;
            String scenario = "";
            String siteCommonName = (String)tm.getValueAt( row, tm.COL_SITE_COMMON_NAME );
            siteCommonName = siteCommonName.replace('.', ' ').replace('-',' ');
            if ( tsType.equalsIgnoreCase("Model") ) {
                loc = "" + (Integer)tm.getValueAt( row, tm.COL_SITE_DATATYPE_ID ) + "-" +
                    (Integer)tm.getValueAt( row, tm.COL_MODEL_RUN_ID );
                String modelName = (String)tm.getValueAt( row, tm.COL_MODEL_NAME );
                modelName = modelName.replace('.', ' ').replace('-',' ');
                String modelRunName = (String)tm.getValueAt( row, tm.COL_MODEL_RUN_NAME );
                modelRunName = modelRunName.replace('.', ' ').replace('-',' ');
                String hydrologicIndicator = (String)tm.getValueAt( row, tm.COL_MODEL_HYDROLOGIC_INDICATOR );
                hydrologicIndicator = hydrologicIndicator.replace('.', ' ').replace('-',' ');
                String modelRunDate = "" + (Date)tm.getValueAt( row, tm.COL_MODEL_RUN_DATE );
                // Trim off the hundredths of a second since that interferes with the TSID conventions.
                // It always appears to be ".0", also remove seconds :00 at end.
                int pos = modelRunDate.indexOf(".");
                if ( pos > 0 ) {
                    modelRunDate = modelRunDate.substring(0,pos - 3);
                }
                // The following should uniquely identify a model time series (in addition to other TSID parts).
                scenario = siteCommonName + "-" + modelName + "-" + modelRunName + "-" + hydrologicIndicator + "-" + modelRunDate;
            }
            else {
                loc = "" + (Integer)tm.getValueAt( row, tm.COL_SITE_DATATYPE_ID );
                scenario = siteCommonName;
            }
            // Use data type common name as FYI and make sure no periods are in it because they will interfere with TSID syntax.
            String dataType = ((String)tm.getValueAt( row, tm.COL_DATA_TYPE_COMMON_NAME)).replace("."," ");
            /* Old code.
            numCommandsAdded = this.tstoolJFrame.queryResultsList_AppendTSIDToCommandList (
                (String)tm.getValueAt( row, tm.COL_OBJECT_TYPE_NAME ) + ":" + loc,
                (String)tm.getValueAt( row, tm.COL_DATA_SOURCE),
                dataType,
                (String)tm.getValueAt( row, tm.COL_TIME_STEP),
                scenario,
                null, // No sequence number.
                (String)tm.getValueAt( row,tm.COL_DATASTORE_NAME),
                "",
                "", false, insertOffset );
            */
            // Create new values using the old code logic.
            String locId = (String)tm.getValueAt( row, tm.COL_OBJECT_TYPE_NAME ) + ":" + loc;
            String source = (String)tm.getValueAt( row, tm.COL_DATA_SOURCE);
            String interval = (String)tm.getValueAt( row, tm.COL_TIME_STEP);
            try {
            	tsid = new TSIdent(locId, source, dataType, interval, scenario, datastoreName, inputName );
    	    }
    	    catch ( Exception e ) {
    	    	throw new RuntimeException ( e );
    	    }
        }
        else {
            String scenario = "";
            if ( tsType.equalsIgnoreCase("Model") ) {
                String modelRunDate = "" + (Date)tm.getValueAt( row, tm.COL_MODEL_RUN_DATE );
                // Trim off the hundredths of a second since that interferes with the TSID conventions.
                // It always appears to be ".0".
                int pos = modelRunDate.indexOf(".");
                if ( pos > 0 ) {
                    modelRunDate = modelRunDate.substring(0,pos);
                }
                // Replace "." with "?" in the model information so as to not conflict with TSID conventions - will switch again later.
                String modelName = (String)tm.getValueAt( row, tm.COL_MODEL_NAME );
                modelName = modelName.replace('.', '?');
                String modelRunName = (String)tm.getValueAt( row, tm.COL_MODEL_RUN_NAME );
                modelRunName = modelRunName.replace('.', '?');
                String hydrologicIndicator = (String)tm.getValueAt( row, tm.COL_MODEL_HYDROLOGIC_INDICATOR );
                hydrologicIndicator = hydrologicIndicator.replace('.', '?');
                // The following should uniquely identify a model time series (in addition to other TSID parts).
                scenario = modelName + "-" + modelRunName + "-" + hydrologicIndicator + "-" + modelRunDate;
            }
            String loc = (String)tm.getValueAt( row, tm.COL_SITE_COMMON_NAME );
            String dataType = (String)tm.getValueAt( row, tm.COL_DATA_TYPE_COMMON_NAME);
            /* Old code
            numCommandsAdded = this.tstoolJFrame.queryResultsList_AppendTSIDToCommandList (
            //(String)__tm.getValueAt( row, model.COL_SUBJECT_TYPE ) + ":" +
            tsType + ":" + loc.replace('.','?'), // Replace period because it will interfere with TSID.
            (String)tm.getValueAt( row, tm.COL_DATA_SOURCE),
            dataType,
            (String)tm.getValueAt( row, tm.COL_TIME_STEP),
            scenario,
            null, // No sequence number.
            (String)tm.getValueAt( row,tm.COL_DATASTORE_NAME),
            "",
            "", false, insertOffset );
            */
            // Create new values using the old code logic.
            String locId = tsType + ":" + loc.replace('.','?'); // Replace the period because it will interfere with TSID.
            String source = (String)tm.getValueAt( row, tm.COL_DATA_SOURCE);
            String interval = (String)tm.getValueAt( row, tm.COL_TIME_STEP);
            try {
            	tsid = new TSIdent(locId, source, dataType, interval, scenario, datastoreName, inputName );
    	    }
    	    catch ( Exception e ) {
    	    	throw new RuntimeException ( e );
    	    }
        }

    	return tsid;
	}

    /**
     * Get the CellRenderer used for displaying the time series in a TableModel.
     */
    @SuppressWarnings("rawtypes")
	public JWorksheet_AbstractExcelCellRenderer getTimeSeriesListCellRenderer(JWorksheet_AbstractRowTableModel tableModel) {
    	return new ReclamationHDB_TimeSeries_CellRenderer ((ReclamationHDB_TimeSeries_TableModel)tableModel);
    }

    /**
     * Get the TableModel used for displaying the time series.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public JWorksheet_AbstractRowTableModel getTimeSeriesListTableModel(List<? extends Object> data) {
    	return new ReclamationHDB_TimeSeries_TableModel(this,(List<ReclamationHDB_SiteTimeSeriesMetadata>)data);
    }

	/**
	Factory method to construct a data store connection from a properties file.
	@param filename name of file containing property strings
	*/
	/* TODO SAM 2015-03-22 This seems to be not needed - if needed, call the factory.create() method.
	public static ReclamationHDBDataStore createFromFile ( String filename )
	throws IOException, Exception {
	    // Read the properties from the file.
	    PropList props = new PropList ("");
	    props.setPersistentName ( filename );
	    props.readPersistent ( false );
	    String name = IOUtil.expandPropertyForEnvironment("Name",props.getValue("Name"));
	    String description = IOUtil.expandPropertyForEnvironment("Description",props.getValue("Description"));
	    String databaseEngine = IOUtil.expandPropertyForEnvironment("DatabaseEngine",props.getValue("DatabaseEngine"));
	    String databaseServer = IOUtil.expandPropertyForEnvironment("DatabaseServer",props.getValue("DatabaseServer"));
	    String databaseName = IOUtil.expandPropertyForEnvironment("DatabaseName",props.getValue("DatabaseName"));
	    String databasePort = IOUtil.expandPropertyForEnvironment("DatabasePort",props.getValue("DatabasePort"));
	    String systemLogin = IOUtil.expandPropertyForEnvironment("SystemLogin",props.getValue("SystemLogin"));
	    String systemPassword = IOUtil.expandPropertyForEnvironment("SystemPassword",props.getValue("SystemPassword"));
	    int port = -1;
	    if ( (databasePort != null) && !databasePort.equals("") ) {
	        try {
	            port = Integer.parseInt(databasePort);
	        }
	        catch ( NumberFormatException e ) {
	            port = -1;
	        }
	    }
	    String keepAliveSql = props.getValue("KeepAliveSQL");
	    String keepAliveFrequency = props.getValue("KeepAliveFrequency");
	    String tsidStyle = props.getValue("TSIDStyle");
	    boolean tsidStyleSDI = true;
	    if ( (tsidStyle != null) && tsidStyle.equalsIgnoreCase("CommonName") ) {
	        tsidStyleSDI = false;
	    }
	    String readNHour = props.getValue("ReadNHourEndDateTime");
	    boolean readNHourEndDateTime = true;
	    if ( (readNHour != null) && readNHour.equalsIgnoreCase("StartDateTimePlusInterval") ) {
	        readNHourEndDateTime = false;
	    }
	    String ConnectTimeout = props.getValue("ConnectTimeout");
	    int connectTimeout = 0;
	    if ( (ConnectTimeout != null) && !ConnectTimeout.equals("") ) {
	        try {
	            connectTimeout = Integer.parseInt(ConnectTimeout);
	        }
	        catch ( Exception e ) {
	            connectTimeout = 0;
	        }
	    }
	    String ReadTimeout = props.getValue("ReadTimeout");
	    int readTimeout = 0;
	    if ( (ReadTimeout != null) && !ReadTimeout.equals("") ) {
	        try {
	            readTimeout = Integer.parseInt(ReadTimeout);
	        }
	        catch ( Exception e ) {
	            readTimeout = 0;
	        }
	    }
	
	    // Get the properties and create an instance
	    ReclamationHDB_DMI dmi = new ReclamationHDB_DMI ( databaseEngine, databaseServer, databaseName, port, systemLogin, systemPassword );
	    dmi.setKeepAlive ( keepAliveSql, keepAliveFrequency ); // Needed for remote access to keep connection open
	    dmi.setTSIDStyleSDI ( tsidStyleSDI );
	    dmi.setReadNHourEndDateTime( readNHourEndDateTime );
	    dmi.setLoginTimeout(connectTimeout);
	    dmi.setReadTimeout(readTimeout);
	    dmi.open();
	    ReclamationHDBDataStore ds = new ReclamationHDBDataStore( name, description, dmi );
	    return ds;
	}
	*/

	/**
	 * Indicate whether the datastore provides a time series input filter.
	 * This datastore does provide an input filter panel.
	 */
	public boolean providesTimeSeriesListInputFilterPanel () {
		return true;
	}

    /**
     * Read a single time series given its time series identifier.
     * @param tsidReq requested time series identifier.
     * The output time series may be different depending on the requested properties.
     * @param readStart start of read, will be set to 'periodStart' service parameter.
     * @param readEnd end of read, will be set to 'periodEnd' service parameter.
     * @return the time series or null if not read
     */
    public TS readTimeSeries ( String tsidReq, DateTime readStart, DateTime readEnd, boolean readData ) {
    	String routine = getClass().getSimpleName() + ".readTimeSeries";
		ReclamationHDB_DMI dmi = (ReclamationHDB_DMI)getDMI();
		if ( dmi == null ) {
			return null;
		}
		else {
			try {
				TS ts = dmi.readTimeSeries(tsidReq, readStart, readEnd, readData);
				return ts;
			}
			catch ( Exception e ) {
				Message.printWarning(3, routine, "Error reading Reclamation HDB time series \"" + tsidReq + "\"" );
				Message.printWarning(3, routine, e );
				return null;
			}
		}
    }

	/**
	Read ReclamationHDB time series metadata to list in the UI.
	*/
	public List<ReclamationHDB_SiteTimeSeriesMetadata> readTimeSeriesMeta ( String dataType, String timeStep, InputFilter_JPanel ifp ) {
		String rtn = getClass().getSimpleName() + ".getTimeSeriesListClicked_ReadReclamationHDBCatalog";
		
		// The headers are a list of ReclamationHDB_SiteTimeSeriesMetadata.
		//try {
			ReclamationHDBDataStore ds = this;
			// Check the connection in case the connection timed out.
			ds.checkDatabaseConnection();
			ReclamationHDB_DMI dmi = (ReclamationHDB_DMI)ds.getDMI();

			if ( timeStep == null ) {
				Message.printWarning ( 1, rtn, "No time series are available for timestep." );
				List<ReclamationHDB_SiteTimeSeriesMetadata> tscatalogList = new ArrayList<>();
				return tscatalogList;
			}
			else {
				timeStep = timeStep.trim();
			}

			List<ReclamationHDB_SiteTimeSeriesMetadata> results = null;
			if ( timeStep.equals("*") ) {
				// Read the time series for each of the major intervals and then concatenate the results.
				String [] timeSteps = {
						this.TIMESTEP_HOUR,
						this.TIMESTEP_DAY,
						this.TIMESTEP_MONTH,
						this.TIMESTEP_YEAR,
						this.TIMESTEP_IRREGULAR
				};
				results = new ArrayList<>();
				List<ReclamationHDB_SiteTimeSeriesMetadata> results2 = null;
				for ( int i = 0; i < timeSteps.length; i++ ) {
					try {
						results2 = dmi.readSiteTimeSeriesMetadataList(dataType, timeSteps[i], ifp);
						if ( results2 != null ) {
							for ( ReclamationHDB_SiteTimeSeriesMetadata result : results2 ) {
								results.add ( result );
							}
						}
					}
					catch ( Exception e ) {
						// Just skip the timestep.
					}
				}
				if ( results.size() == 0 ) {
					results = null; // Handle warning below.
				}
			}
			else {
				// Data type is shown with name so only use the first part of the choice.
				try {
					results = dmi.readSiteTimeSeriesMetadataList(dataType, timeStep, ifp);
				}
				catch ( Exception e ) {
					results = null;
				}
			}

			int size = 0;
			if ( results != null ) {
				size = results.size();
			}
			if ( (results == null) || (size == 0) ) {
				Message.printStatus ( 1, rtn, "Query complete.  No records returned." );
			}
			else {
				Message.printStatus ( 1, rtn, "Query complete. " + size + " records returned." );
			}
		//}
		//catch ( Exception e ) {
		//	// Messages elsewhere but catch so we can get the cursor back.
		//	Message.printWarning ( 3, rtn, e );
		//	JGUIUtil.setWaitCursor ( this.tstoolJFrame, false );
		//}
		return results;
	}
}