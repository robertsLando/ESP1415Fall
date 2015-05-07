package unipd.dei.ESP1415.falldetector.database;


import java.text.SimpleDateFormat;
import java.util.Locale;

import unipd.dei.ESP1415.falldetector.Session;
import unipd.dei.ESP1415.falldetector.database.SessionDB.SessionTable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class helps with database DDL and DML
 * 
 * @author Daniel
 * 
 */
public class DbManager {

	private SQLiteDatabase db;
	private DbHelper dbHelper;
	private Context context;
	private static SimpleDateFormat parser = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);

	public DbManager(Context tempContext) {
		context = tempContext;
		dbHelper = new DbHelper(context);	
	}

	/**
	 * Gets sessions ordered by started date
	 * 
	 * @return A Cursor
	 */
	public Cursor getSessions() {
		final String QUERY_MAIN_VIEW = "SELECT * " + "FROM "
				+ SessionTable.SESSION_TABLE  + " ORDER BY "
				+ SessionTable.START_COLUMN + " DESC";
		db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery(QUERY_MAIN_VIEW, null);

		return c;

	}


	/**
	 * Add a Session into the database
	 * 
	 * @param session
	 *            Session to insert
	 * @return The ID of the session inserted, else -1
	 */
	public long addSession(Session session) {

		long id;
		Long currentTimestamp = System.currentTimeMillis();

		db = dbHelper.getWritableDatabase();
		// Create a new map of values
		ContentValues values = new ContentValues();

		// Insert values with associated column name
		values.put(SessionDB.SessionTable.NAME_COLUMN, session.getName());
		values.put(SessionDB.SessionTable.START_COLUMN, currentTimestamp);
		values.put(SessionDB.SessionTable.END_COLUMN, 0);
		values.put(SessionDB.SessionTable.BGCOLOR_COLUMN, session.getBgColor());
		values.put(SessionDB.SessionTable.IMGCOLOR_COLUMN, session.getImgColor());
		

		// Insert into the table the values
		id = db.insert(SessionDB.SessionTable.SESSION_TABLE, null, values);
		
		if(id == -1) 
			return id;

		// Connect to the database in Readable mode
		db = dbHelper.getReadableDatabase();

		// Fetch the last element inserted
		Cursor c = db.rawQuery("SELECT " + SessionTable.ID_COLUMN + " FROM "
				+ SessionTable.SESSION_TABLE + " ORDER BY " + SessionTable.ID_COLUMN
				+ " DESC LIMIT 1", null);

		if (c.moveToFirst()) {
			return c.getLong(SessionTable.ID);
		}

		return -1;

	}// addSessionAdvance

	/**
	 * Drops and recreates tables
	 */
	public void updateDB() {

		db = dbHelper.getWritableDatabase();
		db.execSQL(SessionTable.SQL_DELETE_ENTRIES);
		db.execSQL(SessionTable.SQL_CREATE_ENTRIES);
	}

	/**
	 * Updates Session status has been terminated
	 * 
	 * @param ID
	 *            The ID of the session in the database
	 */
	public void updateEnd(int ID) {

		Long currentTimestamp = System.currentTimeMillis();
		
		db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		// Insert values with associated column name
		values.put(SessionDB.SessionTable.END_COLUMN, currentTimestamp);

		// update database
		db.update(SessionTable.SESSION_TABLE, values, SessionTable.ID_COLUMN + " = "
				+ ID, null);
	}
	

	/**
	 * Remove a session from the database
	 * 
	 * @param ID
	 *            The ID of the session to remove
	 */
	public void removeSession(int ID) {

		db = dbHelper.getWritableDatabase();
		db.delete(SessionTable.SESSION_TABLE, SessionTable.ID_COLUMN + " = " + ID, null);
	}

	/**
	 * Update a session
	 * 
	 * @param session
	 *            The session with values updated
	 * @param session_deadline
	 *            Updated deadline
	 */
	public void updateSession(Session session) {

		db = dbHelper.getWritableDatabase();
		// Create a new map of values
		ContentValues values = new ContentValues();

		// Insert values with associated column name
		values.put(SessionDB.SessionTable.NAME_COLUMN, session.getName());
		
		// update database
		db.update(SessionTable.SESSION_TABLE, values, SessionTable.ID_COLUMN + " = "
				+ session.getId(), null);

	}// addSession

	
	/**
	 * Get a date (long) and convert it in String by using class Parser methods
	 * @param longDate
	 * @return The date formatted as a String, else null
	 */
	public static String format(long longDate) {

		String date = null;
		
		try {
			
			date = parser.format(longDate);
			
		} catch (IllegalArgumentException e) {
			Log.e(e.getMessage(), "Error while formatting date");
		}
		
		return date;

	}


}// calss: dbManager