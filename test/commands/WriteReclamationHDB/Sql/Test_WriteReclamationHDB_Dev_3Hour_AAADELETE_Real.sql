/* SQL to delete all records in period of test so old data does not interfere with test */
CALL DELETE_R_BASE(100376,'hour',to_date('2010-03-12 00','YYYY-MM-DD HH24'),to_date('2010-03-17 00','YYYY-MM-DD HH24'),7,46)
