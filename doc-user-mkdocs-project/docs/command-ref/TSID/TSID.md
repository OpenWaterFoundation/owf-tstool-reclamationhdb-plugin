# TSTool / Command / TSID for Reclamation HDB #

*   [Overview](#overview)
*   [Command Editor](#command-editor)
*   [Command Syntax](#command-syntax)
*   [Examples](#examples)
*   [Troubleshooting](#troubleshooting)
*   [See Also](#see-also)

-------------------------

## Overview ##

The TSID command for Reclamation HDB causes a single time series to be read from an HDB database.
A TSID command is created by copying a time series from the ***Time Series List*** in the main TSTool interface to the ***Commands*** area.
TSID commands can also be created by editing the command file with a text editor.

See the [Reclamation HDB Datastore Appendix](../../datastore-ref/ReclamationHDB/ReclamationHDB.md) for information about TSID syntax.

See also the [`ReadReclamationHDB`](../ReadReclamationHDB/ReadReclamationHDB.md) command,
which reads one or more time series and provides parameters for control over how data are read.
        
## Command Editor ##

All TSID commands are edited using the general
[`TSID`](https://opencdss.state.co.us/tstool/latest/doc-user/command-ref/TSID/TSID/)
command editor.

## Command Syntax ##

See the [Reclamation HDB Datastore Appendix](../../datastore-ref/ReclamationHDB/ReclamationHDB.md) for information about TSID syntax.

## Examples ##

See the [Reclamation HDB Datastore Appendix](../../datastore-ref/ReclamationHDB/ReclamationHDB.md) for information about TSID syntax.

## Troubleshooting ##

*   See the [`ReadReclamationHDB` command troubleshooting](../ReadReclamationHDB/ReadReclamationHDB.md#troubleshooting) documentation.

## See Also ##

*   [`ReadReclamationHDB`](../ReadReclamationHDB/ReadReclamationHDB.md) command for full control reading HDB time series
*   [`ReadTimeSeries`](https://opencdss.state.co.us/tstool/latest/doc-user/command-ref/ReadTimeSeries/ReadTimeSeries/) command - provides more flexibility than a TSID
