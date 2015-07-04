package unipd.dei.ESP1415.falldetector.falldetailsactivity;

import java.util.Date;

import unipd.dei.ESP1415.falldetector.R;
import unipd.dei.ESP1415.falldetector.R.id;
import unipd.dei.ESP1415.falldetector.R.layout;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.FallDataDB.FallDataTable;
import unipd.dei.ESP1415.falldetector.database.SessionDB.SessionTable;
import unipd.dei.ESP1415.falldetector.sessiondetails.Fall;
import unipd.dei.ESP1415.falldetector.sessiondetails.SessionDetails;
import unipd.dei.ESP1415.falldetector.utilities.Utilities;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FallDetails extends ActionBarActivity {
	
	private Fall currentFall;
	public static Context sdContext;
	private String fallLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fall_event);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		sdContext = this.getBaseContext();	
		Intent myIntent = getIntent();
		currentFall = (Fall) myIntent.getSerializableExtra(SessionDetails.FALL);
		fallLocation = currentFall.getLocation();
         
		long fallID = currentFall.getId(); //I need to know the ID because I need the fall datas
		long sessionID = currentFall.getSessionID();
		System.out.println("ALLORA LA SESSIONE E' : " + sessionID );
		
		//now I have my session ID--> I have to find the imgcolor and the bgcolor of this session
		//I have to find this value in the database
		DbManager databaseManager = new DbManager(this);
		Cursor cIm = databaseManager.getSessionById(sessionID);
		
		//test
		
        cIm.moveToFirst();
        System.out.println("BGCOLOR e' pari a---> " + cIm.getInt(SessionTable.BGCOLOR));
		
		
		final TextView dateFall = (TextView) findViewById(R.id.day);
		final TextView timeFall = (TextView) findViewById(R.id.hour);
		final ImageView sessionImage = (ImageView) findViewById(R.id.sessionImage);
		final TextView location = (TextView) findViewById(R.id.locationAddress);
		
		
		Date fallDate = new Date(currentFall.getDatef());
		
		
		dateFall.setText(Utilities.getOnlyDate(fallDate));
		timeFall.setText(Utilities.getOnlyTime(fallDate));
		location.setText(fallLocation);
		
		//federico
		
		Utilities.setThumbnail(sessionImage, cIm.getInt(SessionTable.BGCOLOR), cIm.getInt(SessionTable.IMGCOLOR));
		
		
		//federico
		//---------XY CHART--------
		
        //DbManager databaseManager = new DbManager(this);
		
		//now I use the accelerometer datas
		Cursor c = databaseManager.getFallData(fallID);
		c.moveToFirst();
		int n = c.getCount();
		
		float[] millis = new float[n];
		float[] acc = new float[n];
		//we fill our array
		for(int i=0; i < n; i++, c.moveToNext()) {
			millis[i]= i;//instead of c.getLong(FallDataTable.TIMEX) / (float)1000;
			acc[i]= c.getFloat(FallDataTable.ACCELERATIONY);
		}
	
		//start to create the xy chart which show the accelerometer data
		LinearLayout image = (LinearLayout) findViewById(R.id.accelerometer_graph);
       
	     ChartXY graph = new ChartXY(this, millis, acc, 1);
	     //we add the graph, which is a view, to the Layout
	     image.addView(graph);
		 
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