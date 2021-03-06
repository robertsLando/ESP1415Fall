package unipd.dei.ESP1415.falldetector.database;

import android.provider.BaseColumns;

/**
 * This class define database informations: table name, columns name, create and
 * drop table query
 */
public final class HelperDB {
	// To prevent someone from accidentally instantiating the Helper class,
	// empty constructor.
	public HelperDB() {
	}
	/*
	 * Inner class that defines the table contents
	 */
	public static abstract class HelperTable implements BaseColumns {
		public static final String HELPER_TABLE = "helper";
		
		/*CREATE TABLE helper (
		 * email TEXT PRIMARY KEY,
		 * name TEXT NOT NULL,
		 * surname TEXT NOT NULL,
		 * priority INTEGER NOT NULL);*/
		
		public static final String EMAIL_COLUMN = "email";
		public static final String NAME_COLUMN = "name";
		public static final String SURNAME_COLUMN = "surname";
		public static final String PRIORITY_COLUMN = "priority";
		
		
		//TYPES STRINGS
		private static final String TEXT_TYPE = " TEXT"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = " PRIMARY KEY";
		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + HELPER_TABLE + " (" 
				+ EMAIL_COLUMN + TEXT_TYPE + PRIMARY_KEY + COMMA_SEP
				+ NAME_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ SURNAME_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ PRIORITY_COLUMN + INTEGER_TYPE + NOT_NULL
				+ ");";
		
		//references
				public static final int EMAIL = 0;
				public static final int NAME = 1;
				public static final int SURNAME = 2;
				public static final int PRIORITY = 3;
				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ HELPER_TABLE;
		

	}

}