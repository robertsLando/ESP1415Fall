package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Set;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.HelperDB.HelperTable;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

public class SelectHelperPreference extends DialogPreference {
	// we have to create a custom MultiSelectListPreference to show all the
	// contacts to the user

	// our MultiSelectListPreference show the name of the helper as entries and
	// the entriesValue are helper's mails
	private Context mContext;
	private ArrayList<String> mName = new ArrayList<String>(); // we use this for saved datas from database
	private ArrayList<String> mMail = new ArrayList<String>();// we use this for saved datas from database
	private ArrayList<String> mSelectedItem; //we save the index of the selected item
	private CharSequence[] mEntries;  //entries
	private CharSequence[] mEntryValues; //entriesValue
	private boolean[] mEntryChecked; //we save if the item is checked or not
	private boolean mPreferenceChanged; //true if preferences are changed
	private Dialog mSelectHelper;

	// constructor
	public SelectHelperPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mSelectHelper = new Dialog(mContext);
		
   }

	// another constructor
	public SelectHelperPreference(Context context) {
		this(context, null);
	}
	

	protected Dialog onCreateDialog(Bundle savedInstanceState) {
		mEntries = entries();
		mEntryValues = entryValues();
		mEntryChecked = new boolean[mEntries.length];
		mSelectedItem = new ArrayList<String>(); //where we track the selected items
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		 
		if(mEntries == null || mEntryValues == null || mEntries.length != mEntryValues.length){
			Toast.makeText(mContext, "You need an Entries Array and an EntriesValue Array", Toast.LENGTH_SHORT).show();
			return null;
		}
		 //ANDROID DOCUMENTATION: Specify the list array, the items to be selected by default (null for none), and the listener through which to receive callbacks when items are selected
		 builder.setMultiChoiceItems(mEntries, mEntryChecked,
                 new DialogInterface.OnMultiChoiceClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        	  if (isChecked) {
                  // If the user checked the item, add it to the selected items
        		  mPreferenceChanged =  mSelectedItem.add(mEntryValues[which].toString());
                  mEntryChecked[which]=true;
              } else if (mSelectedItem.contains(which)) {
                  // Else, if the item is already in the array, remove it 
            	  mPreferenceChanged = mSelectedItem.remove(mEntryValues[which].toString());
                  mEntryChecked[which]=false;
              }
          }
      });
		 return builder.create();

	}
	
	//what happen when the user clicks "OK button" or "CANCEL BUTTON"
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		//if the user clicks OK and he changes the selectionItem
		if(positiveResult && mPreferenceChanged){
			
			//TODO
			
			
			
		}
		
	}
	
	 
	 
	private CharSequence[] entries() {
		// we have to select all the helper from database and how name + surname
		DbManager databaseManager = new DbManager(mContext);
		Cursor c = databaseManager.getHelper();

		while (c.moveToNext()) {
			mName.add(c.getString(HelperTable.NAME) + " "
					+ c.getString(HelperTable.SURNAME));

		}
		CharSequence[] mNameEntry = mName
				.toArray(new CharSequence[mName.size()]);
		return mNameEntry;

	}

	// we create the method entryValues() which associate a value to the entries
	private CharSequence[] entryValues() {
		// we have to select all the helper from the database and put the mail
		// into the ArrayList mail
		DbManager databaseManager = new DbManager(mContext);
		Cursor c = databaseManager.getHelper();
		while (c.moveToNext()) {
			mMail.add(c.getString(HelperTable.EMAIL));
		}
		CharSequence[] mMailEntryValue = mMail.toArray(new CharSequence[mMail
				.size()]);
		return mMailEntryValue;
	}

	

}