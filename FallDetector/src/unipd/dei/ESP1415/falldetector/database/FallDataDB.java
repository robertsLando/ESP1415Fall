package unipd.dei.ESP1415.falldetector.database;

import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
import android.provider.BaseColumns;

/**
 * This class define database informations: table name, columns name, create and
 * drop table query
 */
public final class FallDataDB {
	// To prevent someone from accidentally instantiating the Fall class,
	// empty constructor.
	public FallDataDB() {
	}
	/*
	 * Inner class that defines the table contents
	 */
	public static abstract class FallDataTable implements BaseColumns {
		public static final String FALLDATA_TABLE = "fallData";
		
		/*CREATE TABLE fallData (
		 _id UNSIGNED BIG INT PRIMARY KEY autoincrement,
		 timeX DATETIME NOT NULL,
		 accelerationY FLOAT NOT NULL,
		 fallID INTEGER NOT NULL, 
		 FOREIGN KEY(fallID) REFERENCES fall(_id));
		*/
		
		
		public static final String ID_COLUMN = "_id";
		public static final String TIMESTAMP_COLUMN = "timeX";
		public static final String ACCELERATION_COLUMN = "accelerationY";
		public static final String FALLID_COLUMN = "fallID";

		
		
		//TYPES STRINGS
		private static final String TIMESTAMP_TYPE = " DATETIME"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String FLOAT_TYPE = " FLOAT";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = " PRIMARY KEY";
		private static final String FOREIGN_KEY = " FOREIGN KEY(" + FALLID_COLUMN + ") REFERENCES " + FallTable.FALL_TABLE 
													+ "(" + FallTable.ID_COLUMN + ")";
		
		
		//FOREIGN KEY(customer_id) REFERENCES customers(id)
		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FALLDATA_TABLE + " (" 
				+ ID_COLUMN + INTEGER_TYPE + PRIMARY_KEY + " autoincrement" + COMMA_SEP
				+ TIMESTAMP_COLUMN + TIMESTAMP_TYPE + NOT_NULL + COMMA_SEP
				+ ACCELERATION_COLUMN + FLOAT_TYPE + NOT_NULL + COMMA_SEP
				+ FALLID_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ FOREIGN_KEY
				+ ");";
		
		//references
				public static final int ID = 0;
				public static final int TIMEX = 1;
				public static final int ACCELERATIONY = 2;
				public static final int FALLID = 3;
				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ FALLDATA_TABLE;
		

	}

}