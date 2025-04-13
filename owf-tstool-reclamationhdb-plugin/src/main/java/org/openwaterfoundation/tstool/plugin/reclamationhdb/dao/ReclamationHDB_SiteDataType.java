// ReclamationHDB_SiteDataType - Hold data from the Reclamation HDB database HDB_SITE_DATATYPE table

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

import RTi.DMI.DMIDataObject;
import RTi.DMI.DMIUtil;

/**
Hold data from the Reclamation HDB database HDB_SITE_DATATYPE table,
and also join to HDB_SITE and HDB_DATATYPE to get the common names.
These data are useful for lists and lookups.
*/
public class ReclamationHDB_SiteDataType extends DMIDataObject
{

private int __siteID = DMIUtil.MISSING_INT;
private int __siteDataTypeID = DMIUtil.MISSING_INT;
private int __dataTypeID = DMIUtil.MISSING_INT;
private String __siteCommonName = "";
private String __dataTypeCommonName = "";

/**
Constructor.
*/
public ReclamationHDB_SiteDataType () {
	super();
}

public String getDataTypeCommonName () {
    return __dataTypeCommonName;
}

public int getDataTypeID () {
    return __dataTypeID;
}

public String getSiteCommonName () {
    return __siteCommonName;
}

public int getSiteDataTypeID () {
    return __siteDataTypeID;
}

public int getSiteID () {
    return __siteID;
}

public void setDataTypeCommonName ( String dataTypeCommonName ) {
    __dataTypeCommonName = dataTypeCommonName;
}

public void setDataTypeID ( int dataTypeID ) {
    __dataTypeID = dataTypeID;
}

public void setSiteCommonName ( String siteCommonName ) {
    __siteCommonName = siteCommonName;
}

public void setSiteDataTypeID ( int siteDataTypeID ) {
    __siteDataTypeID = siteDataTypeID;
}

public void setSiteID ( int siteID ) {
    __siteID = siteID;
}

}