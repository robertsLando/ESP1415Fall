package unipd.dei.ESP1415.falldetector;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class SessionDetails extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_details);
		
		Intent myIntent = getIntent(); 
		
		TextView currentSessionName = (TextView) findViewById(R.id.CourrentSessionName);
		TextView sessionStartDate = (TextView) findViewById(R.id.startDateTextView);
		
		long timestamp = myIntent.getLongExtra(SessionViewAdapter.START, 0);
		
		Date sessionDate = new Date(timestamp);
		
		sessionStartDate.setText(SessionViewAdapter.getDate(sessionDate));
		currentSessionName.setText(myIntent.getStringExtra(SessionViewAdapter.NAME));
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.session_details, menu);
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
