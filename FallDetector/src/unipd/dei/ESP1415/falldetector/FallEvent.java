package unipd.dei.ESP1415.falldetector;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class FallEvent extends ActionBarActivity {
	
	private Fall currentFall;
	public static Context sdContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fall_event);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		sdContext = this.getBaseContext();	
		Intent myIntent = getIntent();
		currentFall = (Fall) myIntent.getSerializableExtra(SessionDetails.FALL);
		
		final TextView dateFall = (TextView) findViewById(R.id.day);
		final TextView timeFall = (TextView) findViewById(R.id.hour);
		
		Date fallDate = new Date(currentFall.getDatef());
		
		dateFall.setText(Utilities.getOnlyDate(fallDate));
		timeFall.setText(Utilities.getOnlyTime(fallDate));
		/**
		  *LEFT TO IMPLEMENT CORRECTLY
		  * 
		  *latitudeFall.setText();	
		  *longitudeFall.setText(Utilities.getOnlyTime();
		  */	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.fall_event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
	    case android.R.id.home:
	    	finish();

	        break;

	    default:
	        break;
	    }
		return super.onOptionsItemSelected(item);
	}
}
