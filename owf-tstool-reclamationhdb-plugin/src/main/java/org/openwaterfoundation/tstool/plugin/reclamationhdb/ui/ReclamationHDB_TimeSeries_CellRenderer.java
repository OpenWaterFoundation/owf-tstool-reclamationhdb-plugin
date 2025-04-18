// TSTool_ReclamationHDB_CellRenderer - render cells for TSTool_ReclamationHDB_TableModel data.

/* NoticeStart

TSTool
TSTool is a part of Colorado's Decision Support Systems (CDSS)
Copyright (C) 1994-2025 Colorado Department of Natural Resources

TSTool is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

TSTool is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

You should have received a copy of the GNU General Public License
    along with TSTool.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

package org.openwaterfoundation.tstool.plugin.reclamationhdb.ui;

import RTi.Util.GUI.JWorksheet_AbstractExcelCellRenderer;

/**
This class is used to render cells for TSTool_ReclamationHDB_TableModel data.
*/
@SuppressWarnings("serial")
public class ReclamationHDB_TimeSeries_CellRenderer extends JWorksheet_AbstractExcelCellRenderer {

ReclamationHDB_TimeSeries_TableModel __table_model = null;

/**
Constructor.
@param table_model The TSTool_ReclamationHDB_TableModel to render.
*/
public ReclamationHDB_TimeSeries_CellRenderer ( ReclamationHDB_TimeSeries_TableModel table_model ) {
	__table_model = table_model;
}

/**
Returns the format for a given column.
@param column the column for which to return the format.
@return the column format as used by StringUtil.formatString().
*/
public String getFormat(int column) {
	return __table_model.getFormat(column);
}

/**
Returns the widths of the columns in the table.
@return an integer array of the widths of the columns in the table.
*/
public int[] getColumnWidths() {
	return __table_model.getColumnWidths();
}

}