package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.HelperDB.HelperTable;
import android.content.Context;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

public class SelectHelperPreference extends MultiSelectListPreference {
	//we have to create a custom MultiSelectListPreference to show all the contacts to the user

	//our MultiSelectListPreference show the name of the helper as entries and the entriesValue are helper's mails
	private Context mContext;
	private ArrayList<String> name = new ArrayList<String>(); //entries
	private ArrayList<String> mail = new ArrayList<String>(); //entryValue
	//private boolean[] checkedItems = new boolean[name.size()];

	//constructor 
	public SelectHelperPreference (Context context, AttributeSet attrs){
		super(context, attrs);
		mContext=context;
		setEntries(entries());         
		setEntryValues(entryValues());
	}
	
		//another constructor
	public SelectHelperPreference (Context context){
		this(context,null);
	}
	
	/*
	@Override
	public void onPrepareDialogBuilder(Builder builder){
		 CharSequence[] entries = this.entries();
	     CharSequence[] entryValues = this.entryValues();
	     if (entries == null || entryValues == null || entries.length != entryValues.length) {
	            throw new IllegalStateException(
	                    "MultiSelectListPreference requires an entries array and an entryValues "
	                            + "array which are both the same length");
	      }
	     //restore the item the user checked last time he use this
	    // restoreCheckedEntries();
	     
	     OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
	            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
	                checkedItems[which] = isChecked;
	            }
	     };
	       
		builder.setMultiChoiceItems(entries, checkedItems, listener);
	}*/
	
	//create the method restoreCheckedEntries
	
	
	
	//we create the method entries() which provide to populate the list

	private CharSequence[] entries(){
		//we have to select all the helper from database and how name + surname
		DbManager databaseManager = new DbManager(mContext);
		Cursor c = databaseManager.getHelper();
		
		while(c.moveToNext()){
			name.add(c.getString(HelperTable.NAME) + " " + c.getString(HelperTable.SURNAME));
			
		}
		CharSequence[] nameEntry = name.toArray(new CharSequence[name.size()]);
		return nameEntry;
		
	}
	
	
	//we create the method entryValues() which associate a value to the entries
	private CharSequence[] entryValues(){
		//we have to select all the helper from the database and put the mail into the ArrayList mail
		DbManager databaseManager = new DbManager(mContext);
		Cursor c = databaseManager.getHelper();
		while(c.moveToNext()){
			mail.add(c.getString(HelperTable.EMAIL));
		}
		CharSequence[] mailEntryValue = mail.toArray(new CharSequence[mail.size()]);
		return mailEntryValue;
	}
	
	//now when the user closed the Dialog, we have to save to the SharedPreferences
	
	
}