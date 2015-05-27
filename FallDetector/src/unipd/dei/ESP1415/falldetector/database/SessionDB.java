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
		
		/*CREATE TABLE session (
		 * _id INTEGER PRIMARY KEY autoincrement,
		 * name TEXT NOT NULL,bgcolor INTEGER NOT NULL,
		 * imgcolor INTEGER NOT NULL,
		 * falls INTEGER NOT NULL,
		 * start DATETIME NOT NULL,
		 * end DATETIME NOT NULL,
		 * timeElapsed INTEGER NOT NULL,
		 * isRunning INTEGER NOT NULL);*/
		
		public static final String ID_COLUMN = "_id";
		public static final String NAME_COLUMN = "name";
		public static final String BGCOLOR_COLUMN = "bgcolor";
		public static final String IMGCOLOR_COLUMN = "imgcolor";
		public static final String FALLS_COLUMN = "falls";
		public static final String START_COLUMN = "start";
		public static final String END_COLUMN = "end";
		public static final String TIMEELAPSED_COLUMN = "timeElapsed";
		public static final String ISRUNNING_COLUMN = "isRunning";
		
		
		//TYPES STRINGS
		private static final String TEXT_TYPE = " TEXT";
		private static final String TIMESTAMP_TYPE = " DATETIME"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = " PRIMARY KEY";
		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + SESSION_TABLE + " (" 
				+ ID_COLUMN + INTEGER_TYPE + PRIMARY_KEY + " autoincrement" + COMMA_SEP
				+ NAME_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ BGCOLOR_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ IMGCOLOR_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ FALLS_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ START_COLUMN + TIMESTAMP_TYPE + NOT_NULL + COMMA_SEP
				+ END_COLUMN + TIMESTAMP_TYPE + NOT_NULL + COMMA_SEP
				+ TIMEELAPSED_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ ISRUNNING_COLUMN + INTEGER_TYPE + NOT_NULL
				+ ");";
		
		//references
				public static final int ID = 0;
				public static final int NAME = 1;
				public static final int BGCOLOR = 2;
				public static final int IMGCOLOR = 3;
				public static final int FALLS = 4;
				public static final int START = 5;
				public static final int END = 6;
				public static final int TIMEELAPSED = 7;
				public static final int ISRUNNING = 8;
				
				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ SESSION_TABLE;
		

	}

}