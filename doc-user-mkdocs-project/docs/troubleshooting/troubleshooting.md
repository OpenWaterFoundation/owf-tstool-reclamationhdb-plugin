# TSTool / Troubleshooting #

Troubleshooting TSTool for Reclamation HDB involves confirming that the core product and plugin are performing as expected.
Issues may also be related to HDB data.

*   [Troubleshooting Core TSTool Product](#troubleshooting-core-tstool-product)
*   [Troubleshooting Reclamation HDB TSTool Integration](#troubleshooting-reclamation-hdb-tstool-integration)
    +   [***Commands(Plugin)*** Menu Contains Duplicate Commands](#commandsplugin-menu-contains-duplicate-commands)
    +   [Errors Reading or Writing Time Series](#errors-reading-or-writing-time-series)

If the issue cannot be resolved, contact the support for HDB.

------------------

## Troubleshooting Core TSTool Product ##

See the main [TSTool Troubleshooting documentation](https://opencdss.state.co.us/tstool/latest/doc-user/troubleshooting/troubleshooting/).

## Troubleshooting Reclamation HDB TSTool Integration ##

The following sections summarize typical issues that are encountered when using TSTool with the Reclamation HDB plugin.
Use the following as resources for troubleshooting:

*   The TSTool ***View / Datastores*** menu item displays the status of datastores.
*   The TSTool ***Tools / Diagnostics - View Log File...*** menu item displays the log file.
    A text editor can also be usd to edit the log file.
*   Set the `Debug=True` property in the
    [datastore configuration](../datastore-ref/ReclamationHDB/ReclamationHDB.md#datastore-configuration-file) to turn on more logging messages.
    If the API changes, an error message is typically returned that indicates the problem.

### ***Commands(Plugin)*** Menu Contains Duplicate Commands ###

This should not be an issue if using TSTool 15.0.0 or later because
version 15.0.0 introduced features to manage plugins using versions.
See the ***Tools / Plugin Manager*** menu to to list plugins that are installed.

If the ***Commands(Plugin)*** menu contains duplicate commands,
TSTool is finding multiple plugin `jar` files.
To fix, check the `plugins` folder and subfolders for the software installation folder
and the user's `.tstool/NN/plugins` folder.
Remove extra jar files, leaving only the version that is desired (typically the most recent version).

### Errors Reading or Writing Time Series ###

If there are errors reading or writing time series,
refer to the **Troubleshooting** section for the command with errors.
