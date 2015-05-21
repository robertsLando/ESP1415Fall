package unipd.dei.ESP1415.falldetector;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import android.app.Dialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
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

	private EditText textName;
	private EditText textSurname;
	private EditText textEmail;
	private Spinner setPriority;
	
	// constructor
	public AddHelperPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		addContact = new Dialog(mContext);
		addContact.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setDialogLayoutResource(R.layout.add_contact_dialog);
		setPersistent(false);
	}
	
	// initial values
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		
		textName = (EditText) view.findViewById(R.id.addHelperName);
		textSurname = (EditText) view.findViewById(R.id.addHelperSurname);
		textEmail = (EditText) view.findViewById(R.id.addHelperEmail);
		setPriority = (Spinner) view.findViewById(R.id.addHelperPrioritySpinner);   	
		
	}
	
	//what happens when the user closes the dialog and he clicks on positive button
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		
		if(positiveResult) {
			
		    surname = textSurname.getText().toString();
		    name = textName.getText().toString();
		    email = textEmail.getText().toString();
		    priority = setPriority.getSelectedItemId();
			
			if(name == null || name.equals("")){
				Toast.makeText(mContext,
						"You MUST insert a name",
						Toast.LENGTH_SHORT).show();
				addContact.dismiss();
			}
			
			if(surname == null || surname.equals("")){
				Toast.makeText(mContext,
						"You MUST insert a surname",
						Toast.LENGTH_SHORT).show();
				addContact.dismiss();
			}
			
			
			if(email == null || email.equals("")){
				Toast.makeText(mContext,
						"You MUST insert an email",
						Toast.LENGTH_SHORT).show();
				addContact.dismiss();
				
			}
			
			
			if(priority == -1 || setPriority.getSelectedItemId()== AdapterView.INVALID_ROW_ID){
				Toast.makeText(mContext,
						"You MUST select a priority",
						Toast.LENGTH_SHORT).show();
				addContact.dismiss();
			}
		
			
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