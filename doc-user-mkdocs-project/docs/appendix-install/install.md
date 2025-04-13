# TSTool / Install Reclamation HDB Plugin #

This appendix describes how to install and configure the TSTool Reclamation HDB Plugin.

*   [Install TSTool](#install-tstool)
*   [Install and Configure the TSTool Reclamation HDB Plugin](#install-and-configure-the-tstool-reclamation-hdb-plugin)

-------

## Install TSTool ##

TSTool must be installed before installing the Reclamation HDB plugin.
Typically the latest stable release should be used, although a development version can be installed
in order to use new features.
Multiple versions of TSTool can be installed at the same time.

1.  Download TSTool:
    *   Download the Windows version from the
        [State of Colorado's TSTool Software Downloads](https://opencdss.state.co.us/tstool/) page.
    *   Download the Linux version from the
        [Open Water Foundation TSTool download page](https://software.openwaterfoundation.org/tstool/).
2.  Run the installer and accept defaults.
3.  Run TSTool once by using the ***Start / CDSS / TSTool-Version*** menu on Windows
    (or run the `tstool` program on Linux).
    This will automatically create folders needed to install the plugin.

## Install and Configure the TSTool Reclamation HDB Plugin ##

The plugin installation folder structure is as follows and is explained below.
The convention of using a version folder (e.g., `1.0.0`) was introduced in TSTool 15.0.0.

```
C:\Users\user\.tstool\NN\plugins\owf-tstool-reclamationhdb-plugin\    (Windows)
/home/user/.tstool/NN/plugins/owf-tstool-reclamationhdb-plugin/       (Linux)
  1.0.0/
    owf-tstool-reclamationhdb-plugin-1.0.0.jar
    dep/
```

To install the plugin:

1.  TSTool must have been previously installed and run at least once.
    This will ensure that folders are properly created and, if appropriate,
    a previous version's files will be copied to a new major version run for the first time.
2.  Download the TSTool Reclamation HDB Plugin software installer file from the
    [TSTool Reclamation HDB Download page](https://software.openwaterfoundation.org/tstool-reclamationhdb-plugin/).
    The installer will have a name similar to `tstool-reclamationhdb-plugin-1.0.0-win-202504131326.zip`.
3.  The plugin installation folders are as shown above.
    If installing the plugin in system files on Linux, install in the following folder:
    `/opt/tstool-version/plugins/`
4.  If an old version of the plugin was previous installed and does not exist in a version folder:
    1.  Create a folder with the version (e.g., `1.2.3`) consistent with the software
        and move the files into the folder.
        The files will be available to TSTool versions that are compatible.
    2.  Delete the files if not needed.
5.  Copy files from the `zip` file to the `owf-tstool-reclamationhdb-plugin` folder as shown in the above example:
    *   Windows:  Use File Explorer, 7-Zip, or other software to extract files.
    *   Linux:  Unzip the `zip` file to a temporary folder and copy the files.
6.  Configure one or more datastore configuration files according to the
    [Reclamation HDB Database Datastore](../datastore-ref/ReclamationHDB/ReclamationHDB.md#datastore-configuration-file) documentation.
7.  Restart TSTool.
8.  Test web services access using TSTool by selecting the datastore name that was configured and selecting time series.
9.  If there are issues, use the ***View / Datastores*** menu item to list enabled datastores.
10. If necessary, see the [Troubleshooting](../troubleshooting/troubleshooting.md) documentation.
11. For TSTool 15.0.0 and later, use the TSTool ***Tools / Plugin Manager*** menu to review installed plugins.
