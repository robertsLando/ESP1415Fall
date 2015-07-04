package unipd.dei.ESP1415.falldetector.preferences;

import java.util.ArrayList;
import java.util.List;

import unipd.dei.ESP1415.falldetector.preferences.IDynamicProvider;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.HelperDB.HelperTable;
import android.content.Context;
import android.database.Cursor;

public class DynamicHelperEmails implements IDynamicProvider {
    
	//this class has to give us the entry value of our custom  MultiSeletListPreference
	public List<String> emails;
	
	public DynamicHelperEmails() {
		emails = new ArrayList<String>();
	}
	
	//return the size of array
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		populate();
		return emails.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getItems() {
		// TODO Auto-generated method stub
		return (List<T>) emails;
	}

	@Override
	public void populate() {
		// TODO Auto-generated method stub
		
	}
    
	
	@Override
	public void populate(Context mContext) {
		DbManager databaseManager = new DbManager(mContext);
		Cursor c = databaseManager.getHelper();
		emails.clear();
		while (c.moveToNext()) {
			emails.add(c.getString(HelperTable.EMAIL));
		}
		c.close();
	}

}
