// PluginMetadata - metadata for the TSTool reclamationhdb plugin

/* NoticeStart

OWF TSTool reclamationhdb Plugin
Copyright (C) 2025 Open Water Foundation

OWF TSTool reclamationhdb Plugin is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OWF TSTool reclamationhdb Plugin is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OWF TSTool reclamationhdb Plugin.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

package org.openwaterfoundation.tstool.plugin.reclamationhdb;

public class PluginMeta {
	/**
	 * Plugin version.
	 */
	public static final String VERSION = "1.0.1 (2025-04-16)";

	/**
	 * Get the documentation root URL, used for command help.
	 * This should be the folder in which the index.html file exists, for example:
	 *	  https://software.openwaterfoundation.org/owf-tstool-reclamationhdb-plugin/latest/doc-user/
	 */
	public static String documentationRootUrl() {
		// Hard code for now until figure out how to configure in the META-INF.
		String url = "https://software.openwaterfoundation.org/owf-tstool-reclamationhdb-plugin/latest/doc-user/";
		return url;
	}

}