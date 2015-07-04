package unipd.dei.ESP1415.falldetector.mainactivity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import unipd.dei.ESP1415.falldetector.R;
import unipd.dei.ESP1415.falldetector.Utilities;
import unipd.dei.ESP1415.falldetector.R.id;
import unipd.dei.ESP1415.falldetector.R.layout;
import unipd.dei.ESP1415.falldetector.R.menu;
import unipd.dei.ESP1415.falldetector.sessiondetails.Fall;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class SendEmail extends Activity {
	
	Button buttonSend;
	EditText textTo;
	EditText textCc;
	EditText textBcc;
	EditText textSubject;
	EditText textMessage;
	
	private Fall newFall;
	public static boolean isSendingEmail = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_email);
		
		isSendingEmail = true;
		Intent myIntent = getIntent();
		newFall = (Fall) myIntent.getSerializableExtra(FallService.FALL);
		
		buttonSend = (Button) findViewById(R.id.buttonSend);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage =(EditText) findViewById(R.id.editTextMessage);
		textCc = (EditText) findViewById(R.id.editTextCc);
		textBcc = (EditText) findViewById(R.id.editTextBcc);

		//federico--->use the contact that the user select
	    //ArrayList<String> to = new ArrayList<String>(); //we have to pass an arrayList in our Intent
 	    String[] address;
		Set<String> def = new HashSet<String>();
		def.add(""); //this is the default address---> no address
		SharedPreferences settings = 
		        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Set<String> receiver = settings.getStringSet("contact_list_management_2", def );
		//System.out.println("Il nostro set contiene  : " + receiver.size() + "indirizzi email"); //test to see how many address are in the Set<String>!
		address = new String[receiver.size()];
		int k = 0;
		for(String s : receiver){
			//test to see the addresses selected
			//System.out.println(s); //test for control the address
			//to.add(s);
			address[k++]=s;
		}
		
		/*System.out.println("TEST 2");
		System.out.println("Indirizzi salvati nell'array di stringhe");
		for(int d =0; d<address.length;d++){
			System.out.println(address[d]);
		}*/
		
		
		
		//Retrieving the addressee from the EditText field textTo
		//String to = "djsanco@hotmail.it";
		//Retrieving the mail's subject from the EditText field textSubject
		String subject = "FALL DETECTED! HELP ME";
		//Retrieving the mail's message from the EditText field textMessage
		String message = "Please HELP ME, I fell! \nTime: " + Utilities.getDate(new Date(newFall.getDatef())) + "\nLocation: " + newFall.getLocation();

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, address);
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message);

		//need this to prompts email client only
		email.setType("message/rfc822");

		startActivityForResult((Intent.createChooser(email, "choose an EMAIL client :")),1);
			
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	           isSendingEmail = false; 
	        }
	        if (resultCode == RESULT_CANCELED) {
	           isSendingEmail = false;
	        }
	        
	        Intent intent = new Intent(SendEmail.this, MainActivity.class);
	        startActivity(intent);
	    }
	    
	}//onActivityResult

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_email, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
