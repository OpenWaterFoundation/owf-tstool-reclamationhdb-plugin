# TSTool / Getting Started #

This chapter provides an overview of getting started with TSTool and the Reclamation HDB plugin commands.

*   [Introduction](#introduction)
*   [Installing and Configuring TSTool](#installing-and-configuring-tstool)
*   [Starting TSTool](#starting-tstool)
*   [Installing the Reclamation HDB Plugin](#installing-the-reclamation-hdb-plugin)
*   [Listing Reclamation HDB Time Series](#listing-reclamation-hdb-time-series)
*   [Using the Reclamation HDB Plugin Commands](#using-the-reclamation-hdb-plugin-commands)

----------------

## Introduction ##

The TSTool user interface (UI) provides features to browse data, create command file workflows to process data,
and view time series and other information products.
The TSTool UI design follows several basic concepts and interactions.

*   See the [TSTool Documentation Getting Started](https://opencdss.state.co.us/tstool/latest/doc-user/getting-started/getting-started/)
    documentation for core product information.

The remainder of this documentation focuses on getting started with TSTool using the Reclamation HDB plugin.

## Installing and Configuring TSTool ##

If not already done, [install TSTool](https://opencdss.state.co.us/tstool/latest/doc-user/appendix-install/install/).
TSTool is most often installed on Windows but also runs on Linux.
Contact the Open Water Foundation if a Linux version is required.

## Starting TSTool ##

TSTool can be run in interactive mode and batch (command line) mode.
Interactive mode is typically used to prototype and configure automated workflows and products.
Batch mode can be used to run the software in headless mode on a server,
for example on a schedule or in response to web requests.

When using the State of Colorado’s TSTool default configuration on Windows,
the software can be started using ***Start / Programs / CDSS / TSTool-Version***.
The menus vary slightly depending on the operating system version.

Use the `tstool` script to start the software on Linux.
The program can be found in the software's `/bin` folder.
Multiple versions of TSTool can be installed to facilitate transition from older to newer versions.

To process a command file in batch mode without showing the user interface,
use a command line similar to the following.
The leading `--` are needed to separate options for the TSTool run script from the TSTool application.

```
tstool -- -–commands commands.tstool
```

It is customary to name command files with a `.tstool` file extension.
It may be necessary to specify a full (absolute) path to the command file when
running in batch mode in order for TSTool to fully understand the working directory.

See the [Running TSTool in Various Modes appendix](https://opencdss.state.co.us/tstool/latest/doc-user/appendix-running/running/)
for more information about running in batch and other modes.

## Installing the Reclamation HDB Plugin ##

See the [Appendix - Install Plugin](../appendix-install/install.md) documentation for instructions to install the plugin.

## Listing Reclamation HDB Time Series ##

The main TSTool user interface can be used to list HDB time series, as follows:

1.  Select a Reclamation HDB datastore from the ***Datastore*** choices.
2.  Select a data type using the ***Data type*** choices, or select `*` to list all data types
    (will be slower than a single data type).
3.  Select a time step (interval) using the ***Time step*** choices, or select `*` to list all timesteps
    (will be slower than a single time step).
4.  Use the ***Where*** choices to specify time series filters,
    for example to list time series for a specific site or station.
5.  Press the ***Get Time Series List*** button to list time series.

The time series can then be copied to the ***Commands*** area as time series identifier (TSID) commands.

**<p style="text-align: center;">
![time-series-list](time-series-list.png)
</p>**

**<p style="text-align: center;">
HDB Time Series List (<a href="../time-series-list.png">see full-size image)</a>
</p>**

## Using the Reclamation HDB Plugin Commands ##

TSTool Reclamation HDB plugin commands are provided to read time series from and write time series to HDB databases.
The primary uses of the plugin commands are:

*   read time series from an HDB database to allow processing by other TSTool commands
*   automate visualizing HDB time series
*   combine time series from HDB and other sources

The following is a summary of plugin commands,
which are listed in the ***Commmands(Plugin)*** menu.

**<p style="text-align: center;">
Reclamation HDB Plugin Commands
</p>**

| **Command** | **Description** |
| -- | -- |
| [`ReadReclamationHDB`](../command-ref/ReadReclamationHDB/ReadReclamationHDB.md) | Read one or more time series from an HDB database. |
| [`WriteReclamationHDB`](../command-ref/WriteReclamationHDB/WriteReclamationHDB.md) | Write one or more time series to an HDB database. |
| [`TSID`](../command-ref/TSID/TSID.md) | TSID command format for HDB time series. |
