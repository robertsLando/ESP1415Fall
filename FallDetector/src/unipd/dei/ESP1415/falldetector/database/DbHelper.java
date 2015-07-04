package unipd.dei.ESP1415.falldetector.database;

import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
import unipd.dei.ESP1415.falldetector.database.FallDataDB.FallDataTable;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class support database manage
 */
public class DbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Session.db";

	/**
	 * Calls SQLiteOpenHelper constructor to initialize a new database
	 *
	 * @param context
	 *            the context
	 */
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db){
		
		db.execSQL("PRAGMA foreign_keys=ON"); //foreign key support
		
	}

	/**
	 * When an instance is created this method creates database tables
	 *
	 * @param db
	 *            the database
	 */
	public void onCreate(SQLiteDatabase db) {
    	
    	try{
    	db.execSQL(SessionDB.SessionTable.SQL_CREATE_ENTRIES); //for sessions manage
        db.execSQL(HelperDB.HelperTable.SQL_CREATE_ENTRIES); //to manage the people to report the fall
    	db.execSQL(FallDB.FallTable.SQL_CREATE_ENTRIES); //fall events in a session
        db.execSQL(ReportedToDB.ReportedToTable.SQL_CREATE_ENTRIES); //relation between fall and helper
        db.execSQL(FallDataDB.FallDataTable.SQL_CREATE_ENTRIES); //fall data for a fall event to create the graph
        db.execSQL("CREATE TRIGGER delete_fallData AFTER DELETE ON " 
				   + FallTable.FALL_TABLE
     	       +  " FOR EACH ROW BEGIN"
     	       +  " DELETE FROM " + FallDataTable.FALLDATA_TABLE + " WHERE " 
     	       + FallDataTable.FALLDATA_TABLE + "." + FallDataTable.FALLID_COLUMN 
     	       + "= old." + FallTable.ID_COLUMN 
     	       +  "; END"); //because delete cascade doesn't works in fallData table
    	}catch(SQLException e)
    	{
    		System.out.println("Error while creating the database");
    	}
    }

	/**
	 * Called when database need upgrades (not implemented yet)
	 *
	 * @param db
	 *            the database
	 * @param oldVersion
	 *            old version number
	 * @param newVersion
	 *            new version number
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL(FallDataDB.FallDataTable.SQL_DELETE_ENTRIES);
		db.execSQL(ReportedToDB.ReportedToTable.SQL_DELETE_ENTRIES);
		db.execSQL(FallDB.FallTable.SQL_DELETE_ENTRIES);
		db.execSQL(SessionDB.SessionTable.SQL_DELETE_ENTRIES);
		db.execSQL(HelperDB.HelperTable.SQL_DELETE_ENTRIES);

		onCreate(db);
	}

	/**
	 * Called when database need downgrades
	 *
	 * @param db
	 *            the database
	 * @param oldVersion
	 *            old version number
	 * @param newVersion
	 *            new version number
	 */
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
