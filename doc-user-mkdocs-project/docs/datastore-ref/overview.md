# TSTool / Datastore Reference / Overview #

*   [Introduction](#introduction)
*   [Reclamation HDB Datastores](#reclamation-hdb-datastores)
*   [Datastore Configuration](#datastore-configuration)

-----

## Introduction ##

This reference section of the documentation provides information about the TSTool datastore
that can be used to access Reclamation HDB database data.

See the [TSTool full documentation](https://opencdss.state.co.us/tstool/latest/doc-user/datastore-ref/overview/) for more information
about all datastores that are supported, including other data sources.

## Reclamation HDB Datastores ##

The following datastores provide access to Reclamation HDB data.

| **Datastore (link to documentation)** | **Technology** | **Contents** |
|--|--|--|
| [Reclamation HDB](ReclamationHDB/ReclamationHDB.md) | Oracle database. | Real-time and historical hydrology data. |

## Datastore Configuration ##

Datastores are configured using datastore configuration files, which are described in the specific appendix.

Built-in (installation) datastore configuration files are located in the software installation `datastores` folder.
User datastore configuration files are located in the user's `.tstool/NN/datastores` folder (where `NN` is the TSTool major version).
Note that the `Name` property in the datastore configuration file defines the datastore name, not the file name.

Use the ***View / Datastores*** menu in TSTool to view datastores that are enabled, in particular to review
configuration errors and to see which configuration file was used for a datastore.

Use the ***Tools / Options*** menu in TSTool to change TSTool configuration properties.
If necessary, edit configuration files with a text editor.
