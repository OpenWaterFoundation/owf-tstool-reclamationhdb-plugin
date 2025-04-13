// ReclamationHDB_SiteTimeSeriesMetadata - Hold data from the Reclamation HDB database that is a join of site,
// object, data type, and time series metadata.

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

package org.openwaterfoundation.tstool.plugin.reclamationhdb.dao;

import java.util.Date;

import RTi.DMI.DMIUtil;

/**
Hold data from the Reclamation HDB database that is a join of site, object, data type, and time series metadata.
*/
public class ReclamationHDB_SiteTimeSeriesMetadata extends ReclamationHDB_Site
{

// Data fields that are not in the database but are important metadata.
// Whether Real or Model.
private String __realModelType = "Real";
// Data interval.
private String __dataInterval = "";

// From HDB_OBJECTTYPE.

private int __objectTypeID = DMIUtil.MISSING_INT;
private String __objectTypeName = "";
private String __objectTypeTag = "";

// HDB_SITE data are in base class.

// From HDB_DATATYPE.
private int __dataTypeID = DMIUtil.MISSING_INT;
private String __dataTypeName= "";
private String __dataTypeCommonName= "";
private String __physicalQuantityName= "";
private String __unitCommonName= "";
private int __agenID = -1; // Reference to HDB_AGEN - carry around because agency abbreviation might be null.
private String __agenAbbrev = ""; // Ideally use this but might be null in database.

// From HDB_SITE_DATATYPE.
private int __siteDataTypeID = DMIUtil.MISSING_INT;

// From HDB_MODEL.
private String __modelName = "";
private int __modelID = DMIUtil.MISSING_INT;

// From REF_MODEL_RUN.
private int __modelRunID = DMIUtil.MISSING_INT;
private String __modelRunName = "";
private String __hydrologicIndicator = "";
private Date __modelRunDate = DMIUtil.MISSING_DATE;

// From REF_ENSEMBLE.
private int __ensembleID = DMIUtil.MISSING_INT;
private String __ensembleName = "";
private int __ensembleAgenID = DMIUtil.MISSING_INT;
private String __ensembleAgenAbbrev = ""; // Lookup in HDB_AGEN using AGEN_ID.
private String __ensembleAgenName = ""; // Lookup in HDB_AGEN using AGEN_ID.
private String __ensembleTraceDomain = "";
private String __ensembleCmmnt = "";

// From REF_ENSEMBLE_TRACE - ENSEMBLE_ID and MODEL_RUN_ID are included above.
private int __ensembleTraceID = DMIUtil.MISSING_INT;
private int __ensembleTraceNumeric = DMIUtil.MISSING_INT;
private String __ensembleTraceName = "";

// From data tables (dates give user an indication of the period and are used to help allocate time series memory).
private Date __startDateTimeMin = DMIUtil.MISSING_DATE;
private Date __startDateTimeMax = DMIUtil.MISSING_DATE;

/**
Constructor.
*/
public ReclamationHDB_SiteTimeSeriesMetadata () {
	super();
}

public int getAgenID () {
    return __agenID;
}

public String getAgenAbbrev () {
    return __agenAbbrev;
}

public String getDataInterval () {
    return __dataInterval;
}

public String getDataTypeCommonName () {
    return __dataTypeCommonName;
}

public int getDataTypeID () {
    return __dataTypeID;
}

public String getDataTypeName () {
    return __dataTypeName;
}

public String getEnsembleAgenAbbrev () {
    return __ensembleAgenAbbrev;
}

public int getEnsembleAgenID () {
    return __ensembleAgenID;
}

public String getEnsembleAgenName () {
    return __ensembleAgenName;
}

public String getEnsembleCmmnt () {
    return __ensembleCmmnt;
}

public int getEnsembleID () {
    return __ensembleID;
}

public String getEnsembleName () {
    return __ensembleName;
}

public String getEnsembleTraceDomain () {
    return __ensembleTraceDomain;
}

public int getEnsembleTraceID () {
    return __ensembleTraceID;
}

public String getEnsembleTraceName () {
    return __ensembleTraceName;
}

public int getEnsembleTraceNumeric () {
    return __ensembleTraceNumeric;
}

public String getHydrologicIndicator () {
    return __hydrologicIndicator;
}

public int getModelID () {
    return __modelID;
}

public String getModelName () {
    return __modelName;
}

public Date getModelRunDate () {
    return __modelRunDate;
}

public int getModelRunID () {
    return __modelRunID;
}

public String getModelRunName () {
    return __modelRunName;
}

public int getObjectTypeID () {
    return __objectTypeID;
}

public String getObjectTypeName () {
    return __objectTypeName;
}

public String getObjectTypeTag () {
    return __objectTypeTag;
}

public String getPhysicalQuantityName () {
    return __physicalQuantityName;
}

public String getRealModelType () {
    return __realModelType;
}

public int getSiteDataTypeID () {
    return __siteDataTypeID;
}

public Date getStartDateTimeMax () {
    return __startDateTimeMax;
}

public Date getStartDateTimeMin () {
    return __startDateTimeMin;
}

/**
Get the time series identifier string corresponding to this instance, without the datastore name.
*/
public String getTSID () {
    String tsType = getRealModelType();
    boolean oldStyle = false;
    if ( oldStyle ) {
    	// TODO SAM 2016-02-15 Remove this once new code checks out - not sure if old will need to be supported anymore.
    	// This was used when the TSID started with REAL: or MODEL:, which is no longer done now that SiteDataTypeID is used.
        String scenario = "";
	    if ( tsType.equalsIgnoreCase("Model") ) {
	        String modelRunDate = "" + getModelRunDate();
	        // Trim off the hundredths of a second since that interferes with the TSID conventions and is not
	        // likely needed to uniquely identify the model time series.
	        int pos = modelRunDate.indexOf(".");
	        if ( pos > 0 ) {
	            modelRunDate = modelRunDate.substring(0,pos);
	        }
	        // Replace "." with "?" in the model information so as to not conflict with TSID conventions - will switch again later.
	        String modelName = getModelName();
	        modelName = modelName.replace('.', '?');
	        String modelRunName = getModelRunName();
	        modelRunName = modelRunName.replace('.', '?');
	        String hydrologicIndicator = getHydrologicIndicator();
	        hydrologicIndicator = hydrologicIndicator.replace('.', '?');
	        // The following should uniquely identify a model time series (in addition to other TSID parts).
	        scenario = "." + modelName + "-" + modelRunName + "-" + hydrologicIndicator + "-" + modelRunDate;
	    }
	    return tsType + ":" + getSiteCommonName().replace('.','?') + ".HDB." +
	        getDataTypeCommonName().replace('.', '?') + "." + getDataInterval() +
	        scenario;
    }
    else {
    	String siteCommonName = getSiteCommonName().replace('.', ' ').replace('-',' ');
    	String dataTypeCommonName = getSiteCommonName().replace('.', ' ');
    	String scenario = "";
    	if ( tsType.equalsIgnoreCase("Model") ) {
    		String modelName = getModelName().replace('.', ' ').replace('-',' ');
            String modelRunName = getModelRunName().replace('.', ' ').replace('-',' ');
            String hydrologicIndicator = getHydrologicIndicator().replace('.', ' ').replace('-',' ');
            String modelRunDate = "" + getModelRunDate();
            // Trim off the hundredths of a second since that interferes with the TSID conventions.
            // It always appears to be ".0", also remove seconds :00 at end.
            int pos = modelRunDate.indexOf(".");
            if ( pos > 0 ) {
                modelRunDate = modelRunDate.substring(0,pos - 3);
            }
            // The following should uniquely identify a model time series (in addition to other TSID parts).
            scenario = "." + siteCommonName + "-" + modelName + "-" + modelRunName + "-" + hydrologicIndicator + "-" + modelRunDate;
    	}
    	return getObjectTypeName() + ":" + getSiteDataTypeID() + ".HDB." + dataTypeCommonName + "."
			+ getDataInterval() + "." + siteCommonName + scenario;
    }
}

public String getUnitCommonName () {
    return __unitCommonName;
}

public void setAgenAbbrev ( String agenAbbrev ) {
    __agenAbbrev = agenAbbrev;
}

public void setAgenID ( int agenID ) {
    __agenID = agenID;
}

public void setDataInterval ( String dataInterval ) {
    __dataInterval = dataInterval;
}

public void setDataTypeCommonName ( String dataTypeCommonName ) {
    __dataTypeCommonName = dataTypeCommonName;
}

public void setDataTypeID ( int dataTypeID ) {
    __dataTypeID = dataTypeID;
}

public void setDataTypeName ( String dataTypeName ) {
    __dataTypeName = dataTypeName;
}

public void setEnsembleAgenAbbrev ( String ensembleAgenAbbrev ) {
    __ensembleAgenAbbrev = ensembleAgenAbbrev;
}

public void setEnsembleAgenID ( int ensembleAgenID ) {
    __ensembleAgenID = ensembleAgenID;
}

public void setEnsembleAgenName ( String ensembleAgenName ) {
    __ensembleAgenName = ensembleAgenName;
}

public void setEnsembleCmmnt ( String ensembleCmmnt ) {
    __ensembleCmmnt = ensembleCmmnt;
}

public void setEnsembleID ( int ensembleID ) {
    __ensembleID = ensembleID;
}

public void setEnsembleName ( String ensembleName ) {
    __ensembleName = ensembleName;
}

public void setEnsembleTraceDomain ( String ensembleTraceDomain ) {
    __ensembleTraceDomain = ensembleTraceDomain;
}

public void setEnsembleTraceID ( int ensembleTraceID ) {
    __ensembleTraceID = ensembleTraceID;
}

public void setEnsembleTraceName ( String ensembleTraceName ) {
    __ensembleTraceDomain = ensembleTraceName;
}

public void setEnsembleTraceNumeric ( int ensembleTraceNumeric ) {
    __ensembleTraceNumeric = ensembleTraceNumeric;
}

public void setHydrologicIndicator ( String hydrologicIndicator ) {
    __hydrologicIndicator = hydrologicIndicator;
}

public void setModelID ( int modelID ) {
    __modelID = modelID;
}

public void setModelName ( String modelName ) {
    __modelName = modelName;
}

public void setModelRunDate ( Date modelRunDate ) {
    __modelRunDate = modelRunDate;
}

public void setModelRunID ( int modelRunID ) {
    __modelRunID = modelRunID;
}

public void setModelRunName ( String modelRunName ) {
    __modelRunName = modelRunName;
}

public void setObjectTypeID ( int objectTypeID ) {
    __objectTypeID = objectTypeID;
}

public void setObjectTypeName ( String objectTypeName ) {
    __objectTypeName = objectTypeName;
}

public void setObjectTypeTag ( String objectTypeTag ) {
    __objectTypeTag = objectTypeTag;
}

public void setPhysicalQuantityName ( String physicalQuantityName ) {
    __physicalQuantityName = physicalQuantityName;
}

public void setRealModelType ( String realModelType ) {
    __realModelType = realModelType;
}

public void setSiteDataTypeID ( int siteDataTypeID ) {
    __siteDataTypeID = siteDataTypeID;
}

public void setStartDateTimeMax ( Date startDateTimeMax ) {
    __startDateTimeMax = startDateTimeMax;
}

public void setStartDateTimeMin ( Date startDateTimeMin ) {
    __startDateTimeMin = startDateTimeMin;
}

public void setUnitCommonName ( String unitCommonName ) {
    __unitCommonName = unitCommonName;
}

}