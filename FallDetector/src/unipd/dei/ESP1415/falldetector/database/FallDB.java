package unipd.dei.ESP1415.falldetector.database;

import unipd.dei.ESP1415.falldetector.database.SessionDB.SessionTable;
import android.provider.BaseColumns;

/**
 * This class define database informations: table name, columns name, create and
 * drop table query
 */
public final class FallDB {
	// To prevent someone from accidentally instantiating the Fall class,
	// empty constructor.
	public FallDB() {
	}
	/*
	 * Inner class that defines the table contents
	 */
	public static abstract class FallTable implements BaseColumns {
		public static final String FALL_TABLE = "fall";
		
		
		/*CREATE TABLE fall (
		 * _id INTEGER PRIMARY KEY autoincrement,
		 * location TEXT NOT NULL,
		 * dateF DATETIME NOT NULL,
		 * sessionID INTEGER NOT NULL,
		 * FOREIGN KEY(sessionID) REFERENCES session(_id) ON DELETE CASCADE)*/
		
		public static final String ID_COLUMN = "_id";
		public static final String LOCATION_COLUMN = "location";
		public static final String DATE_COLUMN = "dateF";
		public static final String SESSIONID_COLUMN = "sessionID";
		
		
		
		//TYPES STRINGS
		private static final String TEXT_TYPE = " TEXT";
		private static final String TIMESTAMP_TYPE = " DATETIME"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = " PRIMARY KEY";
		private static final String FOREIGN_KEY = " FOREIGN KEY(" + SESSIONID_COLUMN + ") REFERENCES " + SessionTable.SESSION_TABLE 
				+ "(" + SessionTable.ID_COLUMN + ")  ON DELETE CASCADE)";

		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FALL_TABLE + " (" 
				+ ID_COLUMN + INTEGER_TYPE + PRIMARY_KEY + " autoincrement" + COMMA_SEP
				+ LOCATION_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ DATE_COLUMN + TIMESTAMP_TYPE + NOT_NULL + COMMA_SEP
				+ SESSIONID_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ FOREIGN_KEY
				+ ";";
		
		//references
				public static final int ID = 0;
				public static final int LOCATION = 1;
				public static final int DATE = 2;
				public static final int SESSIONID = 3;
				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ FALL_TABLE;
		

	}

}