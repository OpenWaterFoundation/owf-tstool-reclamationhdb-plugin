# TSTool Reclamation HDB Plugin / Introduction #

*   [Introduction](#introduction)
*   [TSTool use with Reclamation HDB Database](#tstool-use-with-reclamation-hdb-database)

----------------------

## Introduction ##

TSTool is a powerful software tool that automates time series processing and product generation.
It was originally developed for the State of Colorado to process data for river basin modeling and has since
been enhanced to work with many data sources including:

*   United States Geological Survey (USGS) web service and file formats
*   Natural Resources Conservation Service (NRCS) web services
*   Regional Climate Center (RCC) Applied Climate Information Service (ACIS) web services
*   US Army Corps of Engineers DSS data files
*   others

TSTool is maintained by the Open Water Foundation,
which also enhances the software based on project needs.

*   See the latest [TSTool Documentation](https://opencdss.state.co.us/tstool/latest/doc-user/) to learn about core TSTool features.
*   See the [TSTool Download website](https://opencdss.state.co.us/tstool/) for the most recent software versions and documentation.
*   See the [Reclamation HDB Plugin download page](https://software.openwaterfoundation.org/tstool-reclamationhdb-plugin/).

## TSTool use with Reclamation HDB Database ##

HDB database are implemented by US Bureau of Reclamation offices to store data needed for operations.
The HDB database uses Oracle.
TSTool uses Java JDBC technologies to interface with an HDB database, using a "datastore" design
and datastore configuration file.

*   The [Reclamation HDB datastore documentation](../datastore-ref/ReclamationHDB/ReclamationHDB.md) describes how TSTool integrates with the database.
*   The [`ReadReclamationHDB`](../command-ref/ReadReclamationHDB/ReadReclamationHDB.md) command can be used to read time series,
    in addition to time series identifiers that are generated from the main TSTool interface.
*   The [`WriteReclamationHDB`](../command-ref/WriteReclamationHDB/WriteReclamationHDB.md) command can be used to write time series to the database.
