package unipd.dei.ESP1415.falldetector;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
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

		
		//Retrieving the addressee from the EditText field textTo
		String to = "djsanco@hotmail.it";
		//Retrieving the mail's subject from the EditText field textSubject
		String subject = "FALL DETECTED! HELP ME";
		//Retrieving the mail's message from the EditText field textMessage
		String message = "Please HELP ME, I fell! \nTime: " + Utilities.getDate(new Date(newFall.getDatef())) + "\nLocation: " + newFall.getLocation();

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
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
	    }
	    
	    finish();
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
