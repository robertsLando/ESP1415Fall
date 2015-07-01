package unipd.dei.ESP1415.falldetector;

import java.util.Date;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.FallDataDB.FallDataTable;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
         
		long fallID = currentFall.getId(); //I need to know the ID because I need the fall datas
		
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
		
		//---------XY CHART--------
		
		//this is an array of acceleration data I create to see if my graph works
		//I have to pick up the data from the database 
		/*float[] prova = new float[50];
	    for(int r = 0; r<50; r++)
	    	prova[r]= (float) Math.random()*100;*/
		
		//array for the datas, we now that the buffer that we use have max size = 50
		long[] millis = new long[50];
		float[] acc = new float[50];
		
		DbManager databaseManager = new DbManager(this);
		
		//now I use the accelerometer datas
		Cursor c = databaseManager.getFallData(fallID);
	
		//we fill our array
		int i;
		for(i=0; c.moveToNext(); i++) {
			millis[i]= c.getLong(FallDataTable.TIMEX);
			acc[i]= c.getFloat(FallDataTable.ACCELERATIONY);
		}
		
		long startTime = millis[0]; //time in which we start the acquisition of datas
		
		
	
		//start to create the xy chart which show the accelerometer data
		ImageView image = (ImageView) findViewById(R.id.accelerometer_graph);
		image.setBackgroundResource(R.drawable.graph);
		//empty bitmap where we draw our line
		//public static Bitmap createBitmap (int width, int height, Bitmap.Config config)
		Bitmap mEmptyBmap = Bitmap.createBitmap(1000, 100, Bitmap.Config.ARGB_8888);
		
		//the Bitmap is always required for a Canvas
		Canvas mCanvas = new Canvas(mEmptyBmap);
		
		//now we have to draw in our bitmap
		
		 Paint mPaint = new Paint();
		 mPaint.setColor(Color.RED);
		 
		 //create the path, using the data in the array
		 Path mPath = new Path();
		 mPath.moveTo(0, acc[0]);
		 for(int r = 1; r< acc.length; r++) {		 
			 mPath.lineTo((millis[r]-startTime), acc[r]*10); //*100 to show the graph better
		 }
		
		 
		 mCanvas.drawPath(mPath, mPaint);
        
		 image.setImageBitmap(mEmptyBmap);
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