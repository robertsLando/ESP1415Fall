package unipd.dei.ESP1415.falldetector.database;

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
		
		public static final String ID_COLUMN = "_id";
		public static final String NAME_COLUMN = "name";
		public static final String START_COLUMN = "start";
		public static final String END_COLUMN = "end";
		public static final String X_COLUMN = "x";
		public static final String Y_COLUMN = "y";
		public static final String Z_COLUMN = "z";
		
		
		//TYPES STRINGS
		private static final String TEXT_TYPE = " TEXT";
		private static final String TIMESTAMP_TYPE = " DATETIME"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = " PRIMARY KEY";
		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FallTable.FALL_TABLE + " (" 
				+ FallTable.ID_COLUMN + INTEGER_TYPE + PRIMARY_KEY + " autoincrement" + COMMA_SEP
				+ FallTable.NAME_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ FallTable.START_COLUMN + TIMESTAMP_TYPE + NOT_NULL + COMMA_SEP
				+ FallTable.END_COLUMN + TIMESTAMP_TYPE + NOT_NULL
				+ ");";
		
		//references
				public static final int ID = 0;
				public static final int NAME = 1;
				public static final int START = 2;
				public static final int END = 3;
				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ FallTable.FALL_TABLE;
		

	}

}