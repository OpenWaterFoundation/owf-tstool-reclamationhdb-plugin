/*
Useful SQL that can be pasted in for testing.
*/
select * from hdb_datatype, hdb_site, hdb_site_datatype where hdb_datatype.datatype_id = hdb_site_datatype.datatype_id and
hdb_site.site_id = hdb_site_datatype.site_id and hdb_site_datatype.site_datatype_id = 101351
