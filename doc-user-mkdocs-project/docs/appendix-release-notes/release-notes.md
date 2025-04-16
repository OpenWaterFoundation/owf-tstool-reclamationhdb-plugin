# TSTool / Reclamation HDB Plugin / Release Notes #

Release notes are available for the core TSTool product and plugin.
The core software and plugins are maintained separately and may be updated at different times.

*   [TSTool core product release notes](http://opencdss.state.co.us/tstool/latest/doc-user/appendix-release-notes/release-notes/)
*   [TSTool Version Compatibility](#tstool-version-compatibility)
*   [Release Note Details](#release-note-details)

----

## TSTool Version Compatibility ##

The following table lists TSTool and plugin software version compatibility.

**<p style="text-align: center;">
TSTool and Plugin Version Compatibility
</p>**

| **Plugin Version** | **Required TSTool Version** | **Comments** |
| -- | -- | -- |
| 1.0.0 | >=  15.1.0 | First version of the plugin is compatible with TSTool 15.1.0.  Legacy Reclamation HDB commands have been removed from TSTool core in 15.1.0. |

## Release Note Details ##

Plugin release notes are listed below.
The repository issue for release note item is shown where applicable.

*   [Version 1.0.1](#version-101)
*   [Version 1.0.0](#version-100)

----------

## Version 1.0.1 ##

**Maintenance release to clean up plugin and TSTool integration.**

*   ![change](change.png) [1.0.1] Cleanup initial plugin release:
    +   TSTool main window queries were not implemented but are now functional.
    +   **Additional testing needs to occur before using the
        [`WriteReclamationHDB`](../command-ref/WriteReclamationHDB/WriteReclamationHDB.md) command in full production.**

## Version 1.0.0 ##

**Feature release - initial plugin release.**

*   ![new](new.png) [1.0.0] Initial plugin release:
    +   Reclamation HDB features that were previously included in TSTool core software are now provided in the
        Reclamation HDB plugin.
