package unipd.dei.ESP1415.falldetector.database;

import android.provider.BaseColumns;

/**
 * This class define database informations: table name, columns name, create and
 * drop table query
 */
public final class SessionDB {
	// To prevent someone from accidentally instantiating the Session class,
	// empty constructor.
	public SessionDB() {
	}
	/*
	 * Inner class that defines the table contents
	 */
	public static abstract class SessionTable implements BaseColumns {
		public static final String SESSION_TABLE = "session";
		
		public static final String ID_COLUMN = "_id";
		public static final String NAME_COLUMN = "name";
		public static final String BGCOLOR_COLUMN = "bgcolor";
		public static final String IMGCOLOR_COLUMN = "imgcolor";
		public static final String START_COLUMN = "start";
		public static final String END_COLUMN = "end";
		
		
		//TYPES STRINGS
		private static final String TEXT_TYPE = " TEXT";
		private static final String TIMESTAMP_TYPE = " DATETIME"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = " PRIMARY KEY";
		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + SessionTable.SESSION_TABLE + " (" 
				+ SessionTable.ID_COLUMN + INTEGER_TYPE + PRIMARY_KEY + " autoincrement" + COMMA_SEP
				+ SessionTable.NAME_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ SessionTable.BGCOLOR_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ SessionTable.IMGCOLOR_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ SessionTable.START_COLUMN + TIMESTAMP_TYPE + NOT_NULL + COMMA_SEP
				+ SessionTable.END_COLUMN + TIMESTAMP_TYPE + NOT_NULL
				+ ");";
		
		//references
				public static final int ID = 0;
				public static final int NAME = 1;
				public static final int BGCOLOR = 2;
				public static final int IMGCOLOR = 3;
				public static final int START = 2;
				public static final int END = 4;
				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ SessionTable.SESSION_TABLE;
		

	}

}