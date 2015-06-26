package unipd.dei.ESP1415.falldetector.database;

import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
import unipd.dei.ESP1415.falldetector.database.HelperDB.HelperTable;
import android.provider.BaseColumns;

/**
 * This class define database informations: table name, columns name, create and
 * drop table query
 */
public final class ReportedToDB {
	// To prevent someone from accidentally instantiating the Fall class,
	// empty constructor.
	public ReportedToDB() {
	}
	/*
	 * Inner class that defines the table contents
	 */
	public static abstract class ReportedToTable implements BaseColumns {
		public static final String REPORTEDTO_TABLE = "reportedTo";
		
		/*CREATE TABLE reportedTo (
		 * fallID INTEGER NOT NULL,
		 * helperID TEXT NOT NULL,
		 * PRIMARY KEY (fallID,helperID),
		 * FOREIGN KEY(fallID) REFERENCES fall(_id) ON DELETE CASCADE,
		 * FOREIGN KEY(helperID) REFERENCES helper(email) ON DELETE CASCADE);*/
		
		
		public static final String FALLID_COLUMN = "fallID";
		public static final String HELPERID_COLUMN = "helperID";
		

		
		
		//TYPES STRINGS
		private static final String TEXT_TYPE = " TEXT"; 
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String COMMA_SEP = ",";
		private static final String NOT_NULL = " NOT NULL";
		
		//CONSTRAINT
		private static final String PRIMARY_KEY = "PRIMARY KEY (" + FALLID_COLUMN + "," + HELPERID_COLUMN + ")";
		private static final String FOREIGN_KEYFALL = "FOREIGN KEY(" + FALLID_COLUMN + ") REFERENCES " + FallTable.FALL_TABLE 
													+ "(" + FallTable.ID_COLUMN + ") ON DELETE CASCADE";
		private static final String FOREIGN_HELPER = "FOREIGN KEY(" + HELPERID_COLUMN + ") REFERENCES " + HelperTable.HELPER_TABLE 
				+ "(" + HelperTable.EMAIL_COLUMN + ") ON DELETE CASCADE";
		
		
		//FOREIGN KEY(customer_id) REFERENCES customers(id)
		
		
		static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + REPORTEDTO_TABLE + " (" 
				+ FALLID_COLUMN + INTEGER_TYPE + NOT_NULL + COMMA_SEP
				+ HELPERID_COLUMN + TEXT_TYPE + NOT_NULL + COMMA_SEP
				+ PRIMARY_KEY + COMMA_SEP
				+ FOREIGN_KEYFALL + COMMA_SEP
				+ FOREIGN_HELPER
				+ ");";
		
		//references
				public static final int FALLID = 0;
				public static final int HELPERID = 1;

				
		
		//DELETE
		static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ REPORTEDTO_TABLE;
		

	}

}