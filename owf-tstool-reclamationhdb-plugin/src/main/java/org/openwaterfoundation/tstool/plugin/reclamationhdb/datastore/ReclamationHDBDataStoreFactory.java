// ReclamationHDBDataStoreFactory - Factory to instantiate ReclamationHDBDataStore instances.

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
import java.util.List;

import javax.swing.JFrame;

import org.openwaterfoundation.tstool.plugin.reclamationhdb.ui.ReclamationHDBConnectionUI;

import RTi.Util.IO.IOUtil;
import RTi.Util.IO.PropList;
import RTi.Util.Message.Message;
import riverside.datastore.DataStore;
import riverside.datastore.DataStoreConnectionUIProvider;
import riverside.datastore.DataStoreFactory;

/**
Factory to instantiate ReclamationHDBDataStore instances.
*/
public class ReclamationHDBDataStoreFactory implements DataStoreFactory, DataStoreConnectionUIProvider
{

/**
Create a ReclamationHDBDataStore instance and open the encapsulated ReclamationHDB_DMI using the specified properties.
@param props datastore configuration properties, such as read from the configuration file
*/
public DataStore create ( PropList props ) {
    String routine = getClass().getSimpleName() + ".create";
    String name = props.getValue ( "Name" );
    String description = props.getValue ( "Description" );
    if ( description == null ) {
        description = "";
    }
    String databaseEngine = IOUtil.expandPropertyForEnvironment("DatabaseEngine",props.getValue ( "DatabaseEngine" ));
    String databaseServer = IOUtil.expandPropertyForEnvironment("DatabaseServer",props.getValue ( "DatabaseServer" ));
    String databaseName = IOUtil.expandPropertyForEnvironment("DatabaseName",props.getValue ( "DatabaseName" ));
    String databasePort = IOUtil.expandPropertyForEnvironment("DatabasePort",props.getValue("DatabasePort"));
    String systemLogin = IOUtil.expandPropertyForEnvironment("SystemLogin",props.getValue ( "SystemLogin" ));
    String systemPassword = IOUtil.expandPropertyForEnvironment("SystemPassword",props.getValue ( "SystemPassword" ));
    String tsidStyle = props.getValue("TSIDStyle");
    int port = -1;
    if ( (databasePort != null) && !databasePort.equals("") ) {
        try {
            port = Integer.parseInt(databasePort);
        }
        catch ( NumberFormatException e ) {
            port = -1;
        }
    }
    boolean tsidStyleSDI = true;
    if ( (tsidStyle != null) && tsidStyle.equalsIgnoreCase("CommonName") ) {
        tsidStyleSDI = false;
    }
    String ReadNHourEndDateTime = props.getValue("ReadNHourEndDateTime");
    boolean readNHourEndDateTime = false; // Default is StartDateTimePlusInterval.
    if ( (ReadNHourEndDateTime != null) && ReadNHourEndDateTime.equalsIgnoreCase("EndDateTime") ) {
        readNHourEndDateTime = true;
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
    String ResultSetFetchSize = props.getValue("ResultSetFetchSize");
    int resultSetFetchSize = 10000;
    if ( (ResultSetFetchSize != null) && !ResultSetFetchSize.isEmpty() ) {
    	try {
    		resultSetFetchSize = Integer.parseInt(ResultSetFetchSize);
    	}
    	catch ( NumberFormatException e ) {
    		Message.printWarning(2,routine,"Datastore configuration property ResultSetFetchSize (" +
    				ResultSetFetchSize + ") is invalid - must be an integer - setting to 10000.");
    		resultSetFetchSize = 10000;
    	}
    }
    String WriteToHdbInsertStatementMax = props.getValue("WriteToHdbInsertStatementMax");
    int writeToHdbInsertStatementMax = 10000;
    if ( (WriteToHdbInsertStatementMax != null) && !WriteToHdbInsertStatementMax.isEmpty() ) {
    	try {
    		writeToHdbInsertStatementMax = Integer.parseInt(WriteToHdbInsertStatementMax);
    	}
    	catch ( NumberFormatException e ) {
    		Message.printWarning(2,routine,"Datastore configuration property WriteToHdbInsertStatementMax (" +
    			WriteToHdbInsertStatementMax + ") is invalid - must be an integer - setting to 10000.");
    		writeToHdbInsertStatementMax = 10000;
    	}
    }
    // Create an initial datastore instance here with null DMI placeholder - it will be recreated below with DMI.
    ReclamationHDBDataStore ds = new ReclamationHDBDataStore ( name, description, null );
    // Save the properties for later use (e.g., if changing login) but discard the password since private.
    // The password is set below into the DMI and a reopen of the DMI will use that value.
    PropList props2 = new PropList(props);
    props2.unSet("SystemPassword");
    //Message.printStatus(2,routine,"In factory create, description property is \"" + props2.getValue("Description") + "\" from DS=\"" + ds.getProperty("Description") + "\", all properties:");
    Message.printStatus(2,routine, props2.toString());
    ReclamationHDB_DMI dmi = null;
    try {
        dmi = new ReclamationHDB_DMI (
            databaseEngine, // OK if null, will use Oracle.
            databaseServer, // Required.
            databaseName, // Required.
            port,
            systemLogin,
            systemPassword );
        // Set the datastore here so it has a DMI instance, but DMI instance will not be open.
        ds = new ReclamationHDBDataStore ( name, description, dmi );
        ds.setProperties(props2);
        dmi.setTSIDStyleSDI ( tsidStyleSDI );
        dmi.setReadNHourEndDateTime( readNHourEndDateTime );
        dmi.setLoginTimeout(connectTimeout);
        dmi.setReadTimeout(readTimeout);
        dmi.setResultSetFetchSize(resultSetFetchSize);
        dmi.setWriteToHdbInsertStatementMax(writeToHdbInsertStatementMax);
        // Open the database connection.
        dmi.open();
    }
    catch ( Exception e ) {
        // Don't rethrow an exception because want datastore to be created with unopened DMI.
        Message.printWarning(3,routine,e);
        ds.setStatus(1);
        ds.setStatusMessage("" + e);
    }
    return ds;
}

/**
Open a connection UI dialog that displays the connection information for the database.
This version is used when a prompt is desired to enter database login credentials at start-up, using properties from a datastore configuration file.
@param props properties read from datastore configuration file
@param frame a JFrame to use as the parent of the editor dialog
*/
public DataStore openDataStoreConnectionUI ( PropList props, JFrame frame ) {
	return new ReclamationHDBConnectionUI ( this, props, frame ).getDataStore();
}

/**
Open a connection UI dialog that displays the connection information for the database.
This version is used when (re)connecting to a datastore after initial startup, for example to change users.
@param datastoreList a list of ReclamationHDB datastores that were initially configured but may or may not be active/open.
The user will first pick a datastore to access its properties, and will then enter a new login and password for the database connection.
Properties for the datastores are used in addition to the login and password specified interactively to recreate the database connection.
@param frame a JFrame to use as the parent of the editor dialog
*/
public DataStore openDataStoreConnectionUI ( List<? extends DataStore> datastoreList, JFrame frame ) {
	// TODO SAM 2015-03-22 Need to figure out how to handle the generics mapping - is there a better way?
	List<ReclamationHDBDataStore> datastoreList2 = new ArrayList<ReclamationHDBDataStore>();
	for ( DataStore datastore : datastoreList ) {
		datastoreList2.add((ReclamationHDBDataStore)datastore);
	}
	return new ReclamationHDBConnectionUI ( this, datastoreList2, frame ).getDataStore();
}

}