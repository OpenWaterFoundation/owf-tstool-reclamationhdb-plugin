// WriteReclamationHDB_Command - This class initializes, checks, and runs the WriteReclamationHDB() command.

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

package org.openwaterfoundation.tstool.plugin.reclamationhdb.commands;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.openwaterfoundation.tstool.plugin.reclamationhdb.dao.ReclamationHDB_Agency;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.dao.ReclamationHDB_Ensemble;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.dao.ReclamationHDB_EnsembleTrace;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.datastore.ReclamationHDBDataStore;
import org.openwaterfoundation.tstool.plugin.reclamationhdb.datastore.ReclamationHDB_DMI;

import riverside.datastore.DataStore;
import rti.tscommandprocessor.core.TSCommandProcessor;
import rti.tscommandprocessor.core.TSCommandProcessorUtil;
import rti.tscommandprocessor.core.TSListType;
import RTi.TS.TS;
import RTi.Util.IO.AbstractCommand;
import RTi.Util.Message.Message;
import RTi.Util.Message.MessageUtil;
import RTi.Util.IO.CommandException;
import RTi.Util.IO.CommandLogRecord;
import RTi.Util.IO.CommandPhaseType;
import RTi.Util.IO.CommandProcessor;
import RTi.Util.IO.CommandProcessorRequestResultsBean;
import RTi.Util.IO.CommandStatus;
import RTi.Util.IO.CommandStatusType;
import RTi.Util.IO.CommandWarningException;
import RTi.Util.IO.InvalidCommandParameterException;
import RTi.Util.IO.PropList;
import RTi.Util.String.StringUtil;
import RTi.Util.Time.DateTime;
import RTi.Util.Time.TimeInterval;

/**
public class WriteReclamationHDB_Command extends AbstractCommand
This class initializes, checks, and runs the WriteReclamationHDB() command.
*/
public class WriteReclamationHDB_Command extends AbstractCommand
{
	/**
	 * Possible values for WriteProcedure.
	 */
	protected final String _OLD_WRITE_TO_HDB = "OLD_WRITE_TO_HDB";
	protected final String _WRITE_DATA = "WRITE_DATA";

	/**
	 * Possible values for SqlDateType.
	 */
	protected final String _JavaTimestamp = "JavaTimestamp";
	protected final String _OffsetDateTime = "OffsetDateTime";

/**
Constructor.
*/
public WriteReclamationHDB_Command () {
	super();
	setCommandName ( "WriteReclamationHDB" );
}

/**
Check the command parameter for valid values, combination, etc.
@param parameters The parameters for the command.
@param command_tag an indicator to be used when printing messages, to allow a cross-reference to the original commands.
@param warning_level The warning level to use when printing parse warnings
(recommended is 2 for initialization, and 1 for interactive command editor dialogs).
*/
public void checkCommandParameters ( PropList parameters, String command_tag, int warning_level )
throws InvalidCommandParameterException {
	String TSList = parameters.getValue ( "TSList" );
    String DataStore = parameters.getValue ( "DataStore" );
    String IntervalHint = parameters.getValue ( "IntervalHint" );
    String SiteCommonName = parameters.getValue ( "SiteCommonName" );
    //String DataTypeCommonName = parameters.getValue ( "DataTypeCommonName" );
    String SiteDataTypeID = parameters.getValue ( "SiteDataTypeID" );
    //String ModelName = parameters.getValue ( "ModelName" );
    //String ModelRunName = parameters.getValue ( "ModelRunName" );
    //String HydrologicIndicator = parameters.getValue ( "HydrologicIndicator" );
    String ModelRunDate = parameters.getValue ( "ModelRunDate" );
    String NewModelRunDate = parameters.getValue ( "NewModelRunDate" );
    //String ModelRunID = parameters.getValue ( "ModelRunID" );
    String EnsembleName = parameters.getValue ( "EnsembleName" );
    String NewEnsembleName = parameters.getValue ( "NewEnsembleName" );
    String EnsembleModelName = parameters.getValue ( "EnsembleModelName" );
    String EnsembleTrace = parameters.getValue ( "EnsembleTrace" );
    String EnsembleModelRunDate = parameters.getValue ( "EnsembleModelRunDate" );
    String NewEnsembleModelRunDate = parameters.getValue ( "NewEnsembleModelRunDate" );
    String EnsembleModelRunID = parameters.getValue ( "EnsembleModelRunID" );
    String ValidationFlag = parameters.getValue ( "ValidationFlag" );
    String DataFlags = parameters.getValue ( "DataFlags" );
	String OutputStart = parameters.getValue ( "OutputStart" );
	String OutputEnd = parameters.getValue ( "OutputEnd" );
	String Agency = parameters.getValue ( "Agency" );
	String TimeZone = parameters.getValue ( "TimeZone" );
	String WriteProcedure = parameters.getValue ( "WriteProcedure" );
	String SqlDateType = parameters.getValue ( "SqlDateType" );
	// TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
	//String IntervalOverride = parameters.getValue ( "IntervalOverride" );
	String warning = "";
	String routine = getCommandName() + ".checkCommandParameters";
	String message;

	CommandStatus status = getCommandStatus();
	status.clearLog(CommandPhaseType.INITIALIZATION);

    if ( (DataStore == null) || DataStore.equals("") ) {
        message = "The data store must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the data store." ) );
    }

    // SiteCommonName or SiteDatatypeID is required.

    if ( ((SiteCommonName == null) || SiteCommonName.equals("")) &&
        ((SiteDataTypeID == null) || SiteDataTypeID.equals("")) ) {
        message = "The SiteCommonName or SiteDataTypeID must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the SiteCommonName or SiteDataTypeID." ) );
    }

    /* Now optional.
    if ( (SiteCommonName == null) || SiteCommonName.equals("") ) {
        message = "The site common name must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the site common name." ) );
    }

    if ( (DataTypeCommonName == null) || DataTypeCommonName.equals("") ) {
        message = "The data type common name must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the data type common name." ) );
    }
    */

    // Single model time series.

    if ( (IntervalHint != null) && !IntervalHint.equals("") ) {
        try {
            TimeInterval.parseInterval(IntervalHint);
        }
        catch ( Exception e ) {
            message = "The data interval hint (" + IntervalHint + ") is invalid.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify a valid data interval hint." ) );
        }
    }

    /* Now optional.
    if ( (ModelName == null) || ModelName.equals("") ) {
        message = "The model name must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the model name." ) );
    }

    if ( (ModelRunName == null) || ModelRunName.equals("") ) {
        message = "The model run name must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the model run name." ) );
    }

    if ( (HydrologicIndicator == null) || HydrologicIndicator.equals("") ) {
        message = "The hydrologic indicator must be specified.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the hydrologic indicator." ) );
    }
    */

    if ( (ModelRunDate != null) && !ModelRunDate.equals("") ) {
        // If the date/time has a trailing .0, remove because the parse code does not handle the hundredths.
        int pos = ModelRunDate.indexOf(".0");
        DateTime dt = null;
        if ( pos > 0 ) {
            ModelRunDate = ModelRunDate.substring(0,pos);
        }
        try {
            dt = DateTime.parse(ModelRunDate);
        }
        catch ( Exception e ) {
            message = "The model run date is invalid.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify the model run date as YYYY-MM-DD hh:mm." ) );
        }
        if ( dt != null ) {
            if ( dt.getPrecision() != DateTime.PRECISION_MINUTE ) {
                message = "The model run date must be specified to minute precision.";
                warning += "\n" + message;
                status.addToLog ( CommandPhaseType.INITIALIZATION,
                    new CommandLogRecord(CommandStatusType.FAILURE,
                        message, "Specify the model run date as YYYY-MM-DD hh:mm." ) );
            }
        }
    }

    if ( (NewModelRunDate != null) && !NewModelRunDate.equals("") ) {
        // If the date/time has a trailing .0, remove because the parse code does not handle the hundredths.
        int pos = NewModelRunDate.indexOf(".0");
        DateTime dt = null;
        if ( pos > 0 ) {
            NewModelRunDate = NewModelRunDate.substring(0,pos);
        }
        try {
            dt = DateTime.parse(NewModelRunDate);
        }
        catch ( Exception e ) {
            message = "The new model run date is invalid.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify the new model run date as YYYY-MM-DD hh:mm." ) );
        }
        if ( dt != null ) {
            if ( dt.getPrecision() != DateTime.PRECISION_MINUTE ) {
                message = "The new model run date must be specified to minute precision.";
                warning += "\n" + message;
                status.addToLog ( CommandPhaseType.INITIALIZATION,
                    new CommandLogRecord(CommandStatusType.FAILURE,
                        message, "Specify the new model run date as YYYY-MM-DD hh:mm." ) );
            }
        }
    }

    // Ensemble.

    // Any time series can be written as long as the sequence ID can be determined.
    if ( ((EnsembleName != null) && !EnsembleName.equals("")) ||
        ((NewEnsembleName != null) && !NewEnsembleName.equals("") ||
        ((EnsembleModelRunID != null) && !EnsembleModelRunID.equals(""))) ) {
        // Indicates writing an ensemble.
        // Must specify time series with ensemble ID for consistency.
        // TODO SAM 2013-09-26 Might be able to loosen this constraint because a time series without a sequence ID won't get written.
        if ( !TSList.equalsIgnoreCase(""+TSListType.ENSEMBLE_ID) ) {
            message = "Specifying an ensemble name requires using TSList=" + TSListType.ENSEMBLE_ID;
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify an ensemble ID to write." ) );
        }
        // Ensemble model name must be specified.
        if ( (EnsembleModelName == null) || EnsembleModelName.equals("")) {
            message = "The model name for the ensemble must be specified.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify the model name used with the ensemble time series." ) );
        }
        // Ensemble trace number be specified.
        if ( (EnsembleTrace == null) || EnsembleTrace.equals("")) {
            message = "The ensemble trace identifier must be specified.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify the ensemble time series trace identifier." ) );
        }

        if ( (EnsembleModelRunDate != null) && !EnsembleModelRunDate.equals("") ) {
            // If the date/time has a trailing .0, remove because the parse code does not handle the hundredths.
            // TODO SAM 2013-04-07 Check the precision after parsing to make sure to minute.
            int pos = EnsembleModelRunDate.indexOf(".0");
            DateTime dt = null;
            if ( pos > 0 ) {
                EnsembleModelRunDate = EnsembleModelRunDate.substring(0,pos);
            }
            try {
                dt = DateTime.parse(EnsembleModelRunDate);
            }
            catch ( Exception e ) {
                message = "The ensemble model run date is invalid.";
                warning += "\n" + message;
                status.addToLog ( CommandPhaseType.INITIALIZATION,
                    new CommandLogRecord(CommandStatusType.FAILURE,
                        message, "Specify the ensemble model run date as YYYY-MM-DD hh:mm." ) );
            }
            if ( dt != null ) {
                if ( dt.getPrecision() != DateTime.PRECISION_MINUTE ) {
                    message = "The ensemble model run date must be specified to minute precision.";
                    warning += "\n" + message;
                    status.addToLog ( CommandPhaseType.INITIALIZATION,
                        new CommandLogRecord(CommandStatusType.FAILURE,
                            message, "Specify the ensemble model run date as YYYY-MM-DD hh:mm." ) );
                }
            }
        }

        if ( (NewEnsembleModelRunDate != null) && !NewEnsembleModelRunDate.equals("") ) {
            // If the date/time starts with ${ assume that a property is being provided and don't try to parse.
            if ( !NewEnsembleModelRunDate.startsWith("${") ) {
                // If the date/time has a trailing .0, remove because the parse code does not handle the hundredths.
                // TODO SAM 2013-04-07 Check the precision after parsing to make sure to minute.
                int pos = NewEnsembleModelRunDate.indexOf(".0");
                DateTime dt = null;
                if ( pos > 0 ) {
                    NewEnsembleModelRunDate = NewEnsembleModelRunDate.substring(0,pos);
                }
                try {
                    dt = DateTime.parse(NewEnsembleModelRunDate);
                }
                catch ( Exception e ) {
                    message = "The newensemble model run date is invalid.";
                    warning += "\n" + message;
                    status.addToLog ( CommandPhaseType.INITIALIZATION,
                        new CommandLogRecord(CommandStatusType.FAILURE,
                            message, "Specify the new ensemble model run date as YYYY-MM-DD hh:mm." ) );
                }
                if ( dt != null ) {
                    if ( dt.getPrecision() != DateTime.PRECISION_MINUTE ) {
                        message = "The new ensemble model run date must be specified to minute precision.";
                        warning += "\n" + message;
                        status.addToLog ( CommandPhaseType.INITIALIZATION,
                            new CommandLogRecord(CommandStatusType.FAILURE,
                                message, "Specify the new ensemble model run date as YYYY-MM-DD hh:mm." ) );
                    }
                }
            }
        }
    }

    if ( ValidationFlag != null ) {
        ValidationFlag = ValidationFlag.trim();
        if ( (ValidationFlag.length() != 0) && (!Character.isLetter(ValidationFlag.charAt(0)) ||
            !Character.isUpperCase(ValidationFlag.charAt(0))) ) {
            message = "The validation flag (" + ValidationFlag + ") is not an upper-case letter.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify the validation flag as an uppercase letter." ) );
        }
    }

    if ( (DataFlags != null) && DataFlags.trim().length() > 20 ) {
        message = "The data flags (" + DataFlags + ") must be <= 20 characters.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the data flags as <= 20 characters." ) );
    }

	if ( (OutputStart != null) && !OutputStart.equals("")) {
		try {
		    DateTime datetime1 = DateTime.parse(OutputStart);
			if ( datetime1 == null ) {
				throw new Exception ("bad date");
			}
		}
		catch (Exception e) {
			message = "Output start date/time \"" + OutputStart + "\" is not a valid date/time.";
			warning += "\n" + message;
			status.addToLog ( CommandPhaseType.INITIALIZATION,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Specify a valid output start date/time." ) );
		}
	}
	if ( (OutputEnd != null) && !OutputEnd.equals("")) {
		try {
		    DateTime datetime2 = DateTime.parse(OutputEnd);
			if ( datetime2 == null ) {
				throw new Exception ("bad date");
			}
		}
		catch (Exception e) {
			message = "Output end date/time \"" + OutputEnd + "\" is not a valid date/time.";
			warning += "\n" + message;
				status.addToLog ( CommandPhaseType.INITIALIZATION,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Specify a valid output end date/time." ) );
		}
	}
	// TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
	/*
	if ( (IntervalOverride != null) && IntervalOverride.equals("") ) {
        try {
            TimeInterval.parseInterval(IntervalOverride);
        }
        catch ( Exception e ) {
            // Should not happen because choices are valid.
            message = "The interval override \"" + IntervalOverride + "\" is invalid.";
            warning += "\n" + message;
            status.addToLog(CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(
                CommandStatusType.FAILURE, message, "Specify an interval using the command editor."));
        }
	}
	*/

    if ( (TimeZone == null) || TimeZone.equals("") ) {
        message = "The time zone default for the time series must be specified to ensure proper data loading"
        		+ " (used for hourly and instantaneous interval when time series does not have a time zone).";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the time zone." ) );
    }

    if ( (WriteProcedure != null) && !WriteProcedure.isEmpty() &&
    	!WriteProcedure.equalsIgnoreCase(_OLD_WRITE_TO_HDB) && !WriteProcedure.equalsIgnoreCase(_WRITE_DATA) ) {
        message = "The WriteProcedure parameter (" + WriteProcedure + ") is invalid.";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify WriteProcedure as " + _OLD_WRITE_TO_HDB + " or " + _WRITE_DATA + " (default)." ) );
    }

    if ( ((Agency == null) || Agency.equals("")) && ((WriteProcedure == null) ||
    	WriteProcedure.isEmpty() || WriteProcedure.equalsIgnoreCase(_WRITE_DATA))) {
        message = "The agency must be specified when using WriteProcedure=" + _WRITE_DATA + ".";
        warning += "\n" + message;
        status.addToLog ( CommandPhaseType.INITIALIZATION,
            new CommandLogRecord(CommandStatusType.FAILURE,
                message, "Specify the time zone." ) );
    }

    if ( (SqlDateType != null) && !SqlDateType.isEmpty() &&
        	!SqlDateType.equalsIgnoreCase(_JavaTimestamp) && !SqlDateType.equalsIgnoreCase(_OffsetDateTime) ) {
            message = "The SqlDateType parameter (" + SqlDateType + ") is invalid.";
            warning += "\n" + message;
            status.addToLog ( CommandPhaseType.INITIALIZATION,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Specify SqlDateType as " + _JavaTimestamp + " or " + _OffsetDateTime + " (default)." ) );
        }

	// Check for invalid parameters.
	List<String> validList = new ArrayList<>(33);
	validList.add ( "DataStore" );
    validList.add ( "TSList" );
    validList.add ( "TSID" );
    validList.add ( "EnsembleID" );
    validList.add ( "SiteCommonName" );
    validList.add ( "DataTypeCommonName" );
    validList.add ( "SiteDataTypeID" );
    //valid_Vector.add ( "IntervalHint" );
    validList.add ( "ModelName" );
    validList.add ( "ModelRunName" );
    validList.add ( "HydrologicIndicator" );
    validList.add ( "ModelRunDate" );
    validList.add ( "NewModelRunDate" );
    validList.add ( "ModelRunID" );
    validList.add ( "EnsembleName" );
    validList.add ( "NewEnsembleName" );
    validList.add ( "EnsembleTrace" );
    validList.add ( "EnsembleModelName" );
    validList.add ( "EnsembleModelRunDate" );
    validList.add ( "NewEnsembleModelRunDate" );
    validList.add ( "EnsembleModelRunID" );
    validList.add ( "Agency" );
    validList.add ( "CollectionSystem" );
    validList.add ( "Computation" );
    validList.add ( "Method" );
    validList.add ( "ValidationFlag" );
    validList.add ( "OverwriteFlag" );
    validList.add ( "DataFlags" );
    validList.add ( "TimeZone" );
	validList.add ( "OutputStart" );
	validList.add ( "OutputEnd" );
    validList.add ( "WriteProcedure" );
	validList.add ( "EnsembleIDProperty" );
	validList.add ( "SqlDateType" );
	// TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
	//valid_Vector.add ( "IntervalOverride" );
	warning = TSCommandProcessorUtil.validateParameterNames ( validList, this, warning );

	if ( warning.length() > 0 ) {
		Message.printWarning ( warning_level,
		MessageUtil.formatMessageTag(command_tag,warning_level), routine, warning );
		throw new InvalidCommandParameterException ( warning );
	}
	status.refreshPhaseSeverity(CommandPhaseType.INITIALIZATION,CommandStatusType.SUCCESS);
}

/**
Edit the command.
@param parent The parent JFrame to which the command dialog will belong.
@return true if the command was edited (e.g., "OK" was pressed), and false if not (e.g., "Cancel" was pressed.
*/
public boolean editCommand ( JFrame parent ) {
	// The command will be modified if changed.
	return (new WriteReclamationHDB_JDialog ( parent, this )).ok();
}

/**
Run the command.
@param command_number Command number in sequence.
@exception CommandWarningException Thrown if non-fatal warnings occur (the command could produce some results).
@exception CommandException Thrown if fatal warnings occur (the command could not produce output).
*/
public void runCommand ( int command_number )
throws InvalidCommandParameterException, CommandWarningException, CommandException {
	String routine = getClass().getSimpleName() + ".runCommand", message;
	int warning_level = 2;
	int log_level = 3; // Level for non-user messages for log file.
	String command_tag = "" + command_number;
	int warning_count = 0;

	CommandProcessor processor = getCommandProcessor();
	CommandPhaseType commandPhase = CommandPhaseType.RUN;
	CommandStatus status = getCommandStatus();
    Boolean clearStatus = Boolean.TRUE; // Default.
    try {
    	Object o = processor.getPropContents("CommandsShouldClearRunStatus");
    	if ( o != null ) {
    		clearStatus = (Boolean)o;
    	}
    }
    catch ( Exception e ) {
    	// Should not happen.
    }
    if ( clearStatus ) {
		status.clearLog(commandPhase);
	}

	PropList parameters = getCommandParameters();
    String DataStore = parameters.getValue("DataStore");
	String TSList = parameters.getValue ( "TSList" );
    if ( (TSList == null) || TSList.equals("") ) {
        TSList = TSListType.ALL_TS.toString();
    }
	String TSID = parameters.getValue ( "TSID" );
	if ( (commandPhase == CommandPhaseType.RUN) && (TSID != null) && (TSID.indexOf("${") >= 0) ) {
		TSID = TSCommandProcessorUtil.expandParameterValue(processor, this, TSID);
	}
    String EnsembleID = parameters.getValue ( "EnsembleID" );
	if ( (commandPhase == CommandPhaseType.RUN) && (EnsembleID != null) && (EnsembleID.indexOf("${") >= 0) ) {
		EnsembleID = TSCommandProcessorUtil.expandParameterValue(processor, this, EnsembleID);
	}
    String SiteCommonName = parameters.getValue ( "SiteCommonName" );
    String DataTypeCommonName = parameters.getValue ( "DataTypeCommonName" );
    String SiteDataTypeID = parameters.getValue ( "SiteDataTypeID" );
    Long siteDataTypeID = null;
    if ( StringUtil.isLong(SiteDataTypeID) ) {
        siteDataTypeID = Long.parseLong(SiteDataTypeID);
    }
    String ModelName = parameters.getValue ( "ModelName" );
    String ModelRunName = parameters.getValue ( "ModelRunName" );
    String ModelRunDate = parameters.getValue ( "ModelRunDate" );
    if ( ModelRunDate != null ) {
        // TODO SAM 2013-04-07 Strip off hundredths of second.
    	// Should not be needed now because run date should only go to minute.
        int pos = ModelRunDate.indexOf(".0");
        if ( pos > 0 ) {
            ModelRunDate = ModelRunDate.substring(0,pos);
        }
    }
    // TODO SAM 2017-03-17 why is HydrologicIndictor not used?
    //String HydrologicIndicator = parameters.getValue ( "HydrologicIndicator" );
    String ModelRunID = parameters.getValue ( "ModelRunID" );
    Long modelRunID = null;
    if ( (ModelRunID != null) && !ModelRunID.equals("") ) {
        modelRunID = Long.parseLong(ModelRunID);
    }
    String EnsembleName = parameters.getValue ( "EnsembleName" );
    if ( (EnsembleName != null) && EnsembleName.equals("") ) {
        EnsembleName = null; // Simplifies logic below when ensemble is not used.
    }
    String NewEnsembleName = parameters.getValue ( "NewEnsembleName" );
    if ( (NewEnsembleName != null) && NewEnsembleName.equals("") ) {
        NewEnsembleName = null; // Simplifies logic below when ensemble is not used.
    }
    String EnsembleModelName = parameters.getValue ( "EnsembleModelName" );
    String EnsembleTrace = parameters.getValue ( "EnsembleTrace" );
    if ( (EnsembleTrace != null) && EnsembleTrace.equals("") ) {
        EnsembleTrace = "%z"; // Default.
    }
    String EnsembleModelRunDate = parameters.getValue ( "EnsembleModelRunDate" );
    if ( (EnsembleModelRunDate != null) && EnsembleModelRunDate.equals("") ) {
        EnsembleModelRunDate = null; // Simplifies logic below when default is used.
    }
    DateTime ensembleModelRunDate = null;
    if ( (EnsembleModelRunDate != null) && !EnsembleModelRunDate.equals("") ) {
        ensembleModelRunDate = DateTime.parse(EnsembleModelRunDate);
    }
    String EnsembleModelRunID = parameters.getValue ( "EnsembleModelRunID" );
    Long ensembleModelRunID = null;
    if ( (EnsembleModelRunID != null) && !EnsembleModelRunID.equals("") ) {
        ensembleModelRunID = Long.parseLong(EnsembleModelRunID);
    }
    String NewEnsembleModelRunDate = parameters.getValue ( "NewEnsembleModelRunDate" );
    String Agency = parameters.getValue ( "Agency" );
    String CollectionSystem = parameters.getValue ( "CollectionSystem" );
    String Computation = parameters.getValue ( "Computation" );
    String Method = parameters.getValue ( "Method" );
    String ValidationFlag = parameters.getValue ( "ValidationFlag" );
    String OverwriteFlag = parameters.getValue ( "OverwriteFlag" );
    String DataFlags = parameters.getValue ( "DataFlags" );
    String TimeZone = parameters.getValue ( "TimeZone" );
    String WriteProcedure = parameters.getValue ( "WriteProcedure" );
    if ( (WriteProcedure == null) || WriteProcedure.isEmpty() ) {
    	WriteProcedure = _WRITE_DATA; // Default is new WRITE_REAL_DATA and WRITE_MODEL_DATA stored procedure.
    }
    String SqlDateType = parameters.getValue ( "SqlDateType" );
    if ( (SqlDateType == null) || SqlDateType.isEmpty() ) {
    	SqlDateType = _OffsetDateTime; // Default is Java 8 OffsetDateTime.
    }
    String EnsembleIDProperty = parameters.getValue ( "EnsembleIDProperty" );
    if ( (EnsembleIDProperty != null) && (commandPhase == CommandPhaseType.RUN) && EnsembleIDProperty.indexOf("${") >= 0 ) {
    	EnsembleIDProperty = TSCommandProcessorUtil.expandParameterValue(processor, this, EnsembleIDProperty);
    }
    // TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
    /*
    String IntervalOverride = parameters.getValue ( "IntervalOverride" );
    TimeInterval intervalOverride = null;
    if ( (IntervalOverride != null) && !IntervalOverride.equals("") ) {
        try {
            intervalOverride = TimeInterval.parseInterval(IntervalOverride);
        }
        catch ( Exception e ) {
            intervalOverride = null;
        }
    }
    */

	// Get the time series to process.
	PropList request_params = new PropList ( "" );
	request_params.set ( "TSList", TSList );
	request_params.set ( "TSID", TSID );
    request_params.set ( "EnsembleID", EnsembleID );
	CommandProcessorRequestResultsBean bean = null;
	try {
        bean = processor.processRequest( "GetTimeSeriesToProcess", request_params);
	}
	catch ( Exception e ) {
        message = "Error requesting GetTimeSeriesToProcess(TSList=\"" + TSList +
        "\", TSID=\"" + TSID + "\", EnsembleID=\"" + EnsembleID + "\") from processor.";
		Message.printWarning(warning_level,
				MessageUtil.formatMessageTag( command_tag, ++warning_count),
				routine, message );
		status.addToLog ( CommandPhaseType.RUN,
				new CommandLogRecord(CommandStatusType.FAILURE,
						message, "Report problem to software support." ) );
	}
	PropList bean_PropList = bean.getResultsPropList();
	Object o_TSList = bean_PropList.getContents ( "TSToProcessList" );
	if ( o_TSList == null ) {
        message = "Null TSToProcessList returned from processor for GetTimeSeriesToProcess(TSList=\"" + TSList +
        "\" TSID=\"" + TSID + "\", EnsembleID=\"" + EnsembleID + "\").";
		Message.printWarning ( warning_level,
		MessageUtil.formatMessageTag(
		command_tag,++warning_count), routine, message );
		status.addToLog ( CommandPhaseType.RUN,
				new CommandLogRecord(CommandStatusType.FAILURE,
						message, "Report problem to software support." ) );
	}
	@SuppressWarnings("unchecked")
	List<TS> tslist = (List <TS>)o_TSList;
	if ( tslist.size() == 0 ) {
        message = "No time series are available from processor GetTimeSeriesToProcess (TSList=\"" + TSList +
        "\" TSID=\"" + TSID + "\", EnsembleID=\"" + EnsembleID + "\").";
		Message.printWarning ( warning_level,
		MessageUtil.formatMessageTag(command_tag,++warning_count), routine, message );
		status.addToLog ( CommandPhaseType.RUN,
				new CommandLogRecord(CommandStatusType.WARNING,
						message, "Confirm that time series are available (may be OK for partial run)." ) );
	}

	String OutputStart = parameters.getValue ( "OutputStart" );
	DateTime OutputStart_DateTime = null;
	if ( OutputStart != null ) {
		request_params = new PropList ( "" );
		request_params.set ( "DateTime", OutputStart );
		try {
		    bean = processor.processRequest( "DateTime", request_params);
		}
		catch ( Exception e ) {
			message = "Error requesting DateTime(DateTime=" + OutputStart + ") from processor.";
			Message.printWarning(warning_level,
					MessageUtil.formatMessageTag( command_tag, ++warning_count),
					routine, message );
			status.addToLog ( CommandPhaseType.RUN,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Report problem to software support." ) );
		}
		bean_PropList = bean.getResultsPropList();
		Object prop_contents = bean_PropList.getContents ( "DateTime" );
		if ( prop_contents == null ) {
			message = "Null value for DateTime(DateTime=" + OutputStart +
				"\") returned from processor.";
			Message.printWarning(warning_level,
				MessageUtil.formatMessageTag( command_tag, ++warning_count),
				routine, message );
			status.addToLog ( CommandPhaseType.RUN,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Report problem to software support." ) );
		}
		else {
		    OutputStart_DateTime = (DateTime)prop_contents;
		}
	}
	else {
	    // Get from the processor (can be null).
		try {
		    Object o_OutputStart = processor.getPropContents ( "OutputStart" );
			if ( o_OutputStart != null ) {
				OutputStart_DateTime = (DateTime)o_OutputStart;
			}
		}
		catch ( Exception e ) {
			message = "Error requesting OutputStart from processor - not using.";
			Message.printDebug(10, routine, message );
			status.addToLog ( CommandPhaseType.RUN,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Report problem to software support." ) );
		}
	}
	String OutputEnd = parameters.getValue ( "OutputEnd" );
	DateTime OutputEnd_DateTime = null;
	if ( OutputEnd != null ) {
		request_params = new PropList ( "" );
		request_params.set ( "DateTime", OutputEnd );
		try {
		    bean = processor.processRequest( "DateTime", request_params);
		}
		catch ( Exception e ) {
			message = "Error requesting DateTime(DateTime=" + OutputEnd + ") from processor.";
			Message.printWarning(warning_level,
					MessageUtil.formatMessageTag( command_tag, ++warning_count),
					routine, message );
			status.addToLog ( CommandPhaseType.RUN,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Report problem to software support." ) );
		}
		bean_PropList = bean.getResultsPropList();
		Object prop_contents = bean_PropList.getContents ( "DateTime" );
		if ( prop_contents == null ) {
			message = "Null value for DateTime(DateTime=" + OutputEnd +
			"\") returned from processor.";
			Message.printWarning(warning_level,
				MessageUtil.formatMessageTag( command_tag, ++warning_count),
				routine, message );
			status.addToLog ( CommandPhaseType.RUN,
					new CommandLogRecord(CommandStatusType.FAILURE,
							message, "Report problem to software support." ) );
		}
		else {
		    OutputEnd_DateTime = (DateTime)prop_contents;
		}
	}
	else {
	    // Get from the processor.
		try {
		    Object o_OutputEnd = processor.getPropContents ( "OutputEnd" );
			if ( o_OutputEnd != null ) {
				OutputEnd_DateTime = (DateTime)o_OutputEnd;
			}
		}
		catch ( Exception e ) {
			// Not fatal, but of use to developers.
			message = "Error requesting OutputEnd from processor - not using.";
			Message.printDebug(10, routine, message );
		}
	}

    // Write the time series or ensemble.

    try {
        // Find the data store to use.
        DataStore dataStore = ((TSCommandProcessor)processor).getDataStoreForName (
            DataStore, ReclamationHDBDataStore.class );
        if ( dataStore == null ) {
            message = "Could not get datastore for name \"" + DataStore + "\" to query data.";
            Message.printWarning ( 2, routine, message );
            status.addToLog ( CommandPhaseType.RUN,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Verify that a ReclamationHDB datastore has been enabled with name \"" +
                    DataStore + "\"." ) );
            throw new CommandException ( message );
        }
        // Check the connection in case the connection timed out.
    	ReclamationHDBDataStore ds = (ReclamationHDBDataStore)dataStore;
    	ds.checkDatabaseConnection();
        ReclamationHDB_DMI dmi = (ReclamationHDB_DMI)ds.getDMI();
        if ( (dmi == null) || !dmi.isOpen() ) {
            message = "Database connection for datastore \"" + DataStore + "\" is not open.";
            Message.printWarning ( 2, routine, message );
            status.addToLog ( CommandPhaseType.RUN,
                new CommandLogRecord(CommandStatusType.FAILURE,
                    message, "Verify that a ReclamationHDB datastore has been enabled with name \"" +
                    DataStore + "\"." ) );
            throw new CommandException ( message );
        }

        Message.printStatus ( 2, routine, "Writing ReclamationHDB time series to data store \"" + dataStore.getName() + "\"" );
        String loadingApp = "TSTool";
        boolean doWrite = true;
        boolean doEnsemble = false; // Default is single time series.
        int traceNumber = 0;
        int its = 0;
        List<String> problems = new ArrayList<>();
        if ( doWrite && (tslist != null) ) {
            // Update the progress.
            for ( TS ts : tslist ) {
                ++its;
                // The following were set to null above if blank strings.
                if ( (EnsembleName != null) || (NewEnsembleName != null) || (ensembleModelRunID != null) ) {
                	doEnsemble = true;
                    message = "Writing ensemble time series " + its + " of " + tslist.size() + " to HDB";
                    notifyCommandProgressListeners ( (its - 1), tslist.size(), (float)-1.0, message );
                    // Writing an ensemble so get the model_run_id for the specific trace using the ensemble information.
                    // First get the trace number based on the EnsembleTrace parameter.
                    // Retrieve from time series properties.
                    String sequenceID = TSCommandProcessorUtil.expandTimeSeriesMetadataString(
                        processor, ts, EnsembleTrace, status, CommandPhaseType.RUN);
                    // If not an integer, throw an exception.
                    // TODO SAM 2013-09-26 Evaluate with Reclamation input whether trace ID that is not an integer can be used.
                    try {
                         traceNumber = Integer.parseInt(sequenceID);
                    }
                    catch ( NumberFormatException e ) {
                        // No sequence number set so skip the time series.
                        message = "Unable to determine trace number from \"" + sequenceID + "\" for \"" +
                            ts.getIdentifierString() + "\" for EnsembleTrace=\"" + EnsembleTrace +
                            "\" - not writing trace.";
                        Message.printWarning ( warning_level,
                            MessageUtil.formatMessageTag(command_tag, ++warning_count),routine, message );
                        Message.printWarning(warning_level, routine, e);
                        status.addToLog ( CommandPhaseType.RUN,
                            new CommandLogRecord(CommandStatusType.FAILURE,
                                message, "Verify that trace number is available in the time series." ) );
                        continue;
                    }
                    // Get the model run number to be used with HDB.
                    // If specified, use the "New" parameter values to create a new trace.
                    // Otherwise, use the standard parameters.
                    // There is really no way to tell from the HDB stored procedure whether a new model run ID is being generated.
                    String ensembleName = EnsembleName;
                    if ( (NewEnsembleName != null) && !NewEnsembleName.equals("") ) {
                        ensembleName = TSCommandProcessorUtil.expandTimeSeriesMetadataString(
                            processor, ts, NewEnsembleName, status, CommandPhaseType.RUN);
                    }
                    // Run date can be one that was selected or a new date.
                    DateTime modelRunDate = ensembleModelRunDate;
                    if ( (NewEnsembleModelRunDate != null) && !NewEnsembleModelRunDate.equals("") ) {
                        // First try to expand using properties.
                        String date = TSCommandProcessorUtil.expandTimeSeriesMetadataString(
                            processor, ts, NewEnsembleModelRunDate, status, CommandPhaseType.RUN);
                        try {
                            // Now parse into date/time to minute precision.
                            // TODO SAM 2013-09-26 Can't pass precision in parse method for some reason.
                            modelRunDate = DateTime.parse(date);
                            modelRunDate.setPrecision(DateTime.PRECISION_MINUTE);
                        }
                        catch ( Exception e ) {
                            message = "Error parsing new ensemble model run date \"" + date + "\" (" + e + ").";
                            Message.printWarning ( warning_level,
                                MessageUtil.formatMessageTag(command_tag, ++warning_count),routine, message );
                            Message.printWarning(warning_level, routine, e);
                            status.addToLog ( CommandPhaseType.RUN,
                                new CommandLogRecord(CommandStatusType.FAILURE,
                                    message, "Verify that the new ensemble model run date is YYYY-MM-DD hh:ss a property that expands to this." ) );
                            continue;
                        }
                    }
                    if ( ensembleModelRunID != null ) {
                        // Ensemble run ID has been specified.
                        modelRunID = ensembleModelRunID;
                    }
                    else {
                        // Get from the procedure.
                        int agenID = -1;
                        if ( (Agency != null) && !Agency.equals("") ) {
                            ReclamationHDB_Agency a = dmi.lookupAgency(dmi.getAgencyList(), Agency);
                            if ( a != null ) {
                                agenID = a.getAgenID();
                            }
                        }
                        try {
                            Message.printStatus(2, routine, "Calling procedure GET_TSTOOL_ENSEMBLE_MRI using ensemble name=\"" +
                                ensembleName + "\" trace number=" + traceNumber + " model name=\"" + EnsembleModelName +
                                "\" run date=" + modelRunDate );
                            modelRunID = dmi.readModelRunIDForEnsembleTrace (
                                ensembleName, // Must be specified.
                                traceNumber, // Determined at runtime and checked above.
                                EnsembleModelName, // Must be specified.
                                modelRunDate, // Can be an existing run date, a new one, or nothing (not used).
                                agenID ); // -1 if Agency parameter is blank.
                            Message.printStatus(2,routine,"Got model run ID=" + modelRunID );
                        }
                        catch ( Exception e ) {
                            message = "Error determining HDB MRI number for time series \"" +
                            ts.getIdentifierString() + "\" ensemble name= \"" + ensembleName + "\" trace number=" +
                            traceNumber + " EnsembleModelName=\"" + EnsembleModelName +
                            "\" and model run date " + modelRunDate + " - not writing trace (" + e + ").";
                            Message.printWarning ( warning_level,
                                MessageUtil.formatMessageTag(command_tag, ++warning_count),routine, message );
                            Message.printWarning(warning_level, routine, e);
                            status.addToLog ( CommandPhaseType.RUN,
                                new CommandLogRecord(CommandStatusType.FAILURE,
                                    message, "Verify that command parameters are correct." ) );
                            continue;
                        }
                    }
                    if ( modelRunID == null ) {
                        message = "Unable to determine HDB MRI number for time series \"" +
                            ts.getIdentifierString() + "\" ensemble name= \"" + ensembleName + "\" trace number=" +
                            traceNumber + " EnsembleModelName=\"" + EnsembleModelName +
                            "\" and model run date " + modelRunDate + " - not writing trace.";
                        Message.printWarning ( warning_level,
                            MessageUtil.formatMessageTag(command_tag, ++warning_count),routine, message );
                        status.addToLog ( CommandPhaseType.RUN,
                            new CommandLogRecord(CommandStatusType.FAILURE,
                                message, "Verify that command parameters are correct." ) );
                        continue;
                    }
                }
                else {
                	message = "Writing single time series to HDB";
                }
                // Now write the trace time series using the model run ID.
                // The SDI will be used before using the site common name, etc. (which likely have not been specified).
                String hydrologicIndicator = null;
                if ( WriteProcedure.equalsIgnoreCase(_OLD_WRITE_TO_HDB) ) {
	                dmi.writeTimeSeries ( ts, loadingApp,
	                    SiteCommonName, DataTypeCommonName, siteDataTypeID,
	                    ModelName, ModelRunName, ModelRunDate, hydrologicIndicator, modelRunID,
	                    Agency, ValidationFlag, OverwriteFlag, DataFlags,
	                    TimeZone, OutputStart_DateTime, OutputEnd_DateTime );//, intervalOverride );
	                    // TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
                }
                else {
                	dmi.writeTimeSeriesUsingWriteData ( ts, loadingApp,
	                    SiteCommonName, DataTypeCommonName, siteDataTypeID,
	                    ModelName, ModelRunName, ModelRunDate, hydrologicIndicator, modelRunID,
	                    Agency, CollectionSystem, Computation, Method, ValidationFlag, OverwriteFlag, DataFlags,
	                    TimeZone, OutputStart_DateTime, OutputEnd_DateTime, SqlDateType, problems );
                }
                // If the EnsembleIDProperty is set, also set as a processor property.
                // This is a slight performance hit but likely will only be used in testing when the ensemble ID is used to read data directly.
                if ( doEnsemble && (EnsembleIDProperty != null) && !EnsembleIDProperty.isEmpty() ) {
                	// First read the ensemble trace
                	List<ReclamationHDB_EnsembleTrace> traceList = dmi.readRefEnsembleTraceList(-1, -1, modelRunID.intValue(), null);
                	if ( traceList.size() == 1 ) {
                    	// Read the ensemble for the ensemble ID in the trace
                		ReclamationHDB_EnsembleTrace trace = traceList.get(0);
                		List<Integer> ensembleIDList = new ArrayList<>();
                		ensembleIDList.add(Integer.valueOf(trace.getEnsembleID()));
                		List<ReclamationHDB_Ensemble> ensembleList = dmi.readRefEnsembleList(null, ensembleIDList, -1);
                		if ( ensembleList.size() == 1 ) {
		                	// Set the processor property.
                			ReclamationHDB_Ensemble ensemble = ensembleList.get(0);
		                    request_params = new PropList ( "" );
		                    request_params.setUsingObject ( "PropertyName", EnsembleIDProperty );
		                    request_params.setUsingObject ( "PropertyValue", Integer.valueOf(ensemble.getEnsembleID()) );
		                    try {
		                        processor.processRequest( "SetProperty", request_params);
		                    }
		                    catch ( Exception e ) {
		                        message = "Error requesting SetProperty(Property=\"" + EnsembleIDProperty + "\") from processor.";
		                        Message.printWarning(log_level,
		                            MessageUtil.formatMessageTag( command_tag, ++warning_count),
		                            routine, message );
		                        status.addToLog ( CommandPhaseType.RUN,
		                            new CommandLogRecord(CommandStatusType.FAILURE,
		                                message, "Determine why ensemble does not exist." ) );
		                    }
                		}
                		else {
                			message = "Expecting 1 REF_ENSEMBLE for esemble_id " +
                				trace.getEnsembleID() + " but found " + traceList.size() + " - can't set EnsembleIDProperty";
                            Message.printWarning(log_level,
                                MessageUtil.formatMessageTag( command_tag, ++warning_count),
                                routine, message );
                            status.addToLog ( CommandPhaseType.RUN,
                                new CommandLogRecord(CommandStatusType.FAILURE,
                                    message, "Determine why ensemble does not exist." ) );
                		}
                	}
                	else {
                		message = "Expecting 1 REF_ENSEMBLE_TRACE for model_id_id " +
                			modelRunID + " but found " + traceList.size() + " - can't set EnsembleIDProperty";
                        Message.printWarning(log_level,
                            MessageUtil.formatMessageTag( command_tag, ++warning_count),
                            routine, message );
                        status.addToLog ( CommandPhaseType.RUN,
                            new CommandLogRecord(CommandStatusType.FAILURE,
                                message, "Determine why ensemble does not exist." ) );
                	}
                }
            }
        }
        for ( String problem : problems ) {
        	// Print as warnings because exceptions would be caught and handled separately.
        	message = "Problem writing to HDB: " + problem;
            Message.printWarning(log_level,
                MessageUtil.formatMessageTag( command_tag, ++warning_count),
                routine, message );
            status.addToLog ( CommandPhaseType.RUN,
                new CommandLogRecord(CommandStatusType.WARNING,
                    message, "Non-fatal warning writing time series." ) );
        }
    }
    catch ( Exception e ) {
        message = "Unexpected error writing time series to Reclamation HDB data store \"" +
            DataStore + "\" (" + e + ")";
        Message.printWarning ( warning_level,
                MessageUtil.formatMessageTag(command_tag, ++warning_count),routine, message );
        Message.printWarning ( 3, routine, e );
        status.addToLog ( CommandPhaseType.RUN,
			new CommandLogRecord(CommandStatusType.FAILURE,
					message, "Check log file for details." ) );
        throw new CommandException ( message );
    }

	status.refreshPhaseSeverity(CommandPhaseType.RUN,CommandStatusType.SUCCESS);
}

/**
Return the string representation of the command.
@param parameters Command parameters as strings.
*/
public String toString ( PropList parameters ) {
	if ( parameters == null ) {
		return getCommandName() + "()";
	}
	String DataStore = parameters.getValue ( "DataStore" );
    String TSList = parameters.getValue ( "TSList" );
    String TSID = parameters.getValue( "TSID" );
    String EnsembleID = parameters.getValue( "EnsembleID" );
    String SiteCommonName = parameters.getValue( "SiteCommonName" );
    String DataTypeCommonName = parameters.getValue( "DataTypeCommonName" );
    String SiteDataTypeID = parameters.getValue( "SiteDataTypeID" );
    //String IntervalHint = parameters.getValue( "IntervalHint" );
    String ModelName = parameters.getValue( "ModelName" );
    String ModelRunName = parameters.getValue( "ModelRunName" );
    String ModelRunDate = parameters.getValue( "ModelRunDate" );
    String NewModelRunDate = parameters.getValue( "NewModelRunDate" );
    String HydrologicIndicator = parameters.getValue( "HydrologicIndicator" );
    String ModelRunID = parameters.getValue( "ModelRunID" );
    String EnsembleName = parameters.getValue( "EnsembleName" );
    String NewEnsembleName = parameters.getValue( "NewEnsembleName" );
    String EnsembleTrace = parameters.getValue( "EnsembleTrace" );
    String EnsembleModelName = parameters.getValue( "EnsembleModelName" );
    String EnsembleModelRunDate = parameters.getValue( "EnsembleModelRunDate" );
    String NewEnsembleModelRunDate = parameters.getValue( "NewEnsembleModelRunDate" );
    String EnsembleModelRunID = parameters.getValue( "EnsembleModelRunID" );
    String Agency = parameters.getValue( "Agency" );
    String CollectionSystem = parameters.getValue( "CollectionSystem" );
    String Computation = parameters.getValue( "Computation" );
    String Method = parameters.getValue( "Method" );
    String ValidationFlag = parameters.getValue( "ValidationFlag" );
    String OverwriteFlag = parameters.getValue( "OverwriteFlag" );
    String DataFlags = parameters.getValue( "DataFlags" );
    String TimeZone = parameters.getValue( "TimeZone" );
	String OutputStart = parameters.getValue ( "OutputStart" );
	String OutputEnd = parameters.getValue ( "OutputEnd" );
	String WriteProcedure = parameters.getValue( "WriteProcedure" );
	String EnsembleIDProperty = parameters.getValue ( "EnsembleIDProperty" );
	String SqlDateType = parameters.getValue ( "SqlDateType" );
	// TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
	//String IntervalOverride = parameters.getValue ( "IntervalOverride" );
	StringBuffer b = new StringBuffer ();
	if ( (DataStore != null) && (DataStore.length() > 0) ) {
		if ( b.length() > 0 ) {
			b.append ( "," );
		}
		b.append ( "DataStore=\"" + DataStore + "\"" );
	}
    if ( (TSList != null) && (TSList.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "TSList=" + TSList );
    }
    if ( (TSID != null) && (TSID.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "TSID=\"" + TSID + "\"" );
    }
    if ( (EnsembleID != null) && (EnsembleID.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleID=\"" + EnsembleID + "\"" );
    }
    if ( (SiteCommonName != null) && (SiteCommonName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "SiteCommonName=\"" + SiteCommonName + "\"" );
    }
    if ( (DataTypeCommonName != null) && (DataTypeCommonName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "DataTypeCommonName=\"" + DataTypeCommonName + "\"" );
    }
    if ( (SiteDataTypeID != null) && (SiteDataTypeID.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "SiteDataTypeID=" + SiteDataTypeID );
    }
    /*
    if ( (IntervalHint != null) && (IntervalHint.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "IntervalHint=\"" + IntervalHint + "\"" );
    }
    */
    if ( (ModelName != null) && (ModelName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "ModelName=\"" + ModelName + "\"" );
    }
    if ( (ModelRunName != null) && (ModelRunName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "ModelRunName=\"" + ModelRunName + "\"" );
    }
    if ( (ModelRunDate != null) && (ModelRunDate.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "ModelRunDate=\"" + ModelRunDate + "\"" );
    }
    if ( (NewModelRunDate != null) && (NewModelRunDate.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "NewModelRunDate=\"" + NewModelRunDate + "\"" );
    }
    if ( (HydrologicIndicator != null) && (HydrologicIndicator.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "HydrologicIndicator=\"" + HydrologicIndicator + "\"" );
    }
    if ( (ModelRunID != null) && (ModelRunID.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "ModelRunID=" + ModelRunID );
    }
    if ( (EnsembleName != null) && (EnsembleName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleName=\"" + EnsembleName + "\"" );
    }
    if ( (NewEnsembleName != null) && (NewEnsembleName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "NewEnsembleName=\"" + NewEnsembleName + "\"" );
    }
    if ( (EnsembleTrace != null) && (EnsembleTrace.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleTrace=\"" + EnsembleTrace + "\"" );
    }
    if ( (EnsembleModelName != null) && (EnsembleModelName.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleModelName=\"" + EnsembleModelName + "\"" );
    }
    if ( (EnsembleModelRunDate != null) && (EnsembleModelRunDate.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleModelRunDate=\"" + EnsembleModelRunDate + "\"" );
    }
    if ( (NewEnsembleModelRunDate != null) && (NewEnsembleModelRunDate.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "NewEnsembleModelRunDate=\"" + NewEnsembleModelRunDate + "\"" );
    }
    if ( (EnsembleModelRunID != null) && (EnsembleModelRunID.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleModelRunID=\"" + EnsembleModelRunID + "\"" );
    }
    if ( (Agency != null) && (Agency.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "Agency=\"" + Agency + "\"");
    }
    if ( (CollectionSystem != null) && (CollectionSystem.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "CollectionSystem=\"" + CollectionSystem + "\"");
    }
    if ( (Computation != null) && (Computation.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "Computation=\"" + Computation + "\"");
    }
    if ( (Method != null) && (Method.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "Method=\"" + Method + "\"");
    }
    if ( (ValidationFlag != null) && (ValidationFlag.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "ValidationFlag=\"" + ValidationFlag + "\"" );
    }
    if ( (OverwriteFlag != null) && (OverwriteFlag.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "OverwriteFlag=\"" + OverwriteFlag + "\"" );
    }
    if ( (DataFlags != null) && (DataFlags.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "DataFlags=\"" + DataFlags + "\"" );
    }
    if ( (TimeZone != null) && (TimeZone.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "TimeZone=\"" + TimeZone + "\"" );
    }
    if ( (OutputStart != null) && (OutputStart.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "OutputStart=\"" + OutputStart + "\"" );
    }
    if ( (OutputEnd != null) && (OutputEnd.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "OutputEnd=\"" + OutputEnd + "\"" );
    }
    if ( (WriteProcedure != null) && (WriteProcedure.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "WriteProcedure=\"" + WriteProcedure + "\"" );
    }
    if ( (EnsembleIDProperty != null) && (EnsembleIDProperty.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "EnsembleIDProperty=\"" + EnsembleIDProperty + "\"" );
    }
    if ( (SqlDateType != null) && (SqlDateType.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "SqlDateType=\"" + SqlDateType + "\"" );
    }
    // TODO SAM 2013-04-20 Current thought is irregular data is OK to instantaneous table - remove later.
    /*
    if ( (IntervalOverride != null) && (IntervalOverride.length() > 0) ) {
        if ( b.length() > 0 ) {
            b.append ( "," );
        }
        b.append ( "IntervalOverride=\"" + IntervalOverride + "\"" );
    }
    */
	return getCommandName() + "(" + b.toString() + ")";
}

}