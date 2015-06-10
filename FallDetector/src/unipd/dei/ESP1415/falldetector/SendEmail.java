package unipd.dei.ESP1415.falldetector;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_email);
		
		Intent myIntent = getIntent();
		newFall = (Fall) myIntent.getSerializableExtra(FallService.FALL);
		
		buttonSend = (Button) findViewById(R.id.buttonSend);
		textTo = (EditText) findViewById(R.id.editTextTo);
		textSubject = (EditText) findViewById(R.id.editTextSubject);
		textMessage =(EditText) findViewById(R.id.editTextMessage);
		textCc = (EditText) findViewById(R.id.editTextCc);
		textBcc = (EditText) findViewById(R.id.editTextBcc);

		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				//Retrieving the addressee from the EditText field textTo
				String to = textTo.getText().toString();
				//Retrieving the Cc addressee from the EditText field textCc
				String cc = textCc.getText().toString();
				//Retrieving the Bcc addressee from the EditText field textBcc
				String bcc = textBcc.getText().toString();
				//Retrieving the mail's subject from the EditText field textSubject
				String subject = textSubject.getText().toString();
				//Retrieving the mail's message from the EditText field textMessage
				String message = textMessage.getText().toString();

				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
				if(!cc.isEmpty())
					email.putExtra(Intent.EXTRA_CC, new String[]{ to});
				if(!bcc.isEmpty())
					email.putExtra(Intent.EXTRA_BCC, new String[]{ to});
				if(!subject.isEmpty())
					email.putExtra(Intent.EXTRA_SUBJECT, subject);
				email.putExtra(Intent.EXTRA_TEXT, message);

				//need this to prompts email client only
				email.setType("message/rfc822");

				startActivity(Intent.createChooser(email, "choose an EMAIL client :"));
			}
		});
	}

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
