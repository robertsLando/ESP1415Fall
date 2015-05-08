package unipd.dei.ESP1415.falldetector.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *This class support database manage
 */
public class DbHelper extends SQLiteOpenHelper{
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Session.db";

    /**
     *Calls SQLiteOpenHelper constructor to initialize a new database
     *@param context the context 
     */ 
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    /**
     *When an instance is created this method creates database tables
     *@param db the database
     */ 
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(SessionDB.SessionTable.SQL_CREATE_ENTRIES);
    }
    
    /**
     *Called when database need upgrades
     *@param db the database
     *@param oldVersion old version number
     *@param newVersion new version number
     */ 
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SessionDB.SessionTable.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    /**
     *Called when database need downgrades
     *@param db the database
     *@param oldVersion old version number
     *@param newVersion new version number
     */ 
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
