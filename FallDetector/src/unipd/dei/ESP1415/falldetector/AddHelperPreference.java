package unipd.dei.ESP1415.falldetector;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import android.app.Dialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddHelperPreference extends DialogPreference {
	// this class permit the user to insert name, surname, mail, and a priority
	// level for a contact. We save all infos in the database

	private String name;
	private String surname;
	private String email;
	private long priority;
	private Dialog addContact;
	private Context mContext;

	// constructor
	public AddHelperPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		addContact = new Dialog(mContext);
		setDialogLayoutResource(R.layout.add_contact_dialog);
		
		
	}
	
	

	// initial values
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		name="";
		surname="";
		email="";
		priority=-1;
	}
	
	//what happens when the user closes the dialog and he clicks on positive button
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		final EditText textName = (EditText) addContact.findViewById(R.id.name);
		final EditText textSurname = (EditText) addContact.findViewById(R.id.surname);
		final EditText textEmail = (EditText) addContact.findViewById(R.id.email);
	    final Spinner setPriority = (Spinner) addContact.findViewById(R.id.priority_spinner);
		
		
		if(positiveResult) {
			if(textName.getText().toString().equals("")){
				Toast.makeText(mContext,
						"You MUST insert a name",
						Toast.LENGTH_SHORT).show();
			}
			else
				name = textName.getText().toString();
			
			if(textSurname.getText().toString().equals("")){
				Toast.makeText(mContext,
						"You MUST insert a surname",
						Toast.LENGTH_SHORT).show();
			}
			else
				surname = textSurname.getText().toString();
			
			if(textEmail.getText().toString().equals("")){
				Toast.makeText(mContext,
						"You MUST insert an email",
						Toast.LENGTH_SHORT).show();
				
			}
			else
				email = textEmail.getText().toString();
			if(priority == -1 || setPriority.getSelectedItemId()== AdapterView.INVALID_ROW_ID){
				Toast.makeText(mContext,
						"You MUST select a priority",
						Toast.LENGTH_SHORT).show();
			}
			else
			    priority = setPriority.getSelectedItemId();
			
			Helper temp = new Helper();
			temp.setEmail(email);
			temp.setName(name);
			temp.setSurname(surname);
			temp.setPriority(priority);
			
			DbManager databaseManager = new DbManager(mContext);
			
			long id = databaseManager.addHelper(temp);
			
			if(id == -1) 
				Toast.makeText(mContext, mContext.getString(R.string.error), Toast.LENGTH_SHORT).show();
			
			addContact.dismiss();
			
		}//endPositiveResult
		else
			addContact.dismiss();
			
	}
}