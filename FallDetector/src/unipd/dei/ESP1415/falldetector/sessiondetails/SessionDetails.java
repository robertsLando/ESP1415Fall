package unipd.dei.ESP1415.falldetector.sessiondetails;

import java.util.ArrayList;
import java.util.Date;








import unipd.dei.ESP1415.falldetector.R;
import unipd.dei.ESP1415.falldetector.R.id;
import unipd.dei.ESP1415.falldetector.R.layout;
import unipd.dei.ESP1415.falldetector.R.string;
//import unipd.dei.ESP1415.falldetector.GraphViewer.AccRunner;
//import unipd.dei.ESP1415.falldetector.GraphViewer.MyServiceConnection;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
import unipd.dei.ESP1415.falldetector.falldetailsactivity.FallDetails;
import unipd.dei.ESP1415.falldetector.mainactivity.MainActivity;
import unipd.dei.ESP1415.falldetector.mainactivity.Session;
import unipd.dei.ESP1415.falldetector.mainactivity.SessionViewAdapter;
import unipd.dei.ESP1415.falldetector.utilities.Utilities;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
//import android.content.ServiceConnection;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
//import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SessionDetails extends ActionBarActivity{

	private Activity activity; // the activity where the ListView is placed
	public static Context sdContext;
	ListView list;
	public ScrollView scroll;
	public ArrayList<Fall> fallList = new ArrayList<Fall>();
	public FallViewAdapter adapter = null;
	public static final String FALL = "fall";
	private Session currentSession;
	public View v;
	//private aData data;
	//private FallService mService; // the instance of the service
	public boolean mBound = false;
	private static boolean isRunning = false;
	private Thread accThread;
	//String[] verlabels = new String[] { "10", "0", "-10" };
	public static final String ACCSERVICE = "accservice";
	//public MyBinder binder;
	private SensorManager sensorManager;

	private TextView acc_x;
	private TextView acc_y;
	private TextView acc_z;

	private double ax;
	private double ay;
	private double az;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_session_details);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setHomeAsUpIndicator(
		//		R.drawable.ic_action_remove_white);
		activity = this;
		sdContext = this.getBaseContext();	
		Intent myIntent = getIntent();
		currentSession = (Session) myIntent.getSerializableExtra(SessionViewAdapter.SESSION);
		/*
		 * set of instructions regarding the initialization of the list view
		 * which contains all the falls in this session
		 */
		setListData(currentSession.getId());

		this.adapter = new FallViewAdapter(this,
				R.layout.fall_list_item_layout, this.fallList);

		list = (ListView) findViewById(R.id.sessionListView);
		list.setAdapter(adapter);

		final TextView currentSessionName = (TextView) findViewById(R.id.CourrentSessionName);
		TextView sessionStartDate = (TextView) findViewById(R.id.startDateTextView);
		ImageView image = (ImageView) findViewById(R.id.courrentSessionImage);
		final ImageView btnEdit = (ImageView) findViewById(R.id.editButton);
		final ImageView btnGraph = (ImageView) findViewById(R.id.showGraphButton);
		final ImageView btnGraphON = (ImageView) findViewById(R.id.showGraphButtonON);
		final ImageView btnDone = (ImageView) findViewById(R.id.doneButton);
		final EditText newSessionName = (EditText) findViewById(R.id.newSessionName);
		final Button btnDelete = (Button) findViewById(R.id.courrentSessionBtnDelete);
		acc_x = (TextView) findViewById(R.id.accelerometer_data_x);
		acc_y = (TextView) findViewById(R.id.accelerometer_data_y);
		acc_z = (TextView) findViewById(R.id.accelerometer_data_z);

		/*
		 * only if the session is running (not stopped or paused) 
		 * is possible to see the runtime accelerator data
		 * 
		 */
		
	
		if(currentSession.isRunning())
			btnGraph.setVisibility(View.VISIBLE);
		else
		{	
			acc_x.setVisibility(View.GONE);
			acc_y.setVisibility(View.GONE);
			acc_z.setVisibility(View.GONE);
		}
		
		if(isRunning)
		{
			btnGraphON.setVisibility(View.VISIBLE);
			btnGraph.setVisibility(View.GONE);
			acc_x.setVisibility(View.VISIBLE);
			acc_y.setVisibility(View.VISIBLE);
			acc_z.setVisibility(View.VISIBLE);
			if(accThread == null)
			{
				accThread = new Thread(new AccRunner(acc_x, acc_y, acc_z));
				accThread.start();
				isRunning = true;
			}
		}
		
		

		/*
		 * accelerometer
		 */

		btnGraph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){

				btnGraph.setVisibility(View.GONE);
				btnGraphON.setVisibility(View.VISIBLE);

				acc_x.setVisibility(View.VISIBLE);
				acc_y.setVisibility(View.VISIBLE);
				acc_z.setVisibility(View.VISIBLE);
				
				sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
				sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


				accThread = new Thread(new AccRunner(acc_x, acc_y, acc_z));
				accThread.start();
				isRunning = true;
			}
		});

		btnGraphON.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){

				btnGraph.setVisibility(View.VISIBLE);
				btnGraphON.setVisibility(View.GONE);

				acc_x.setVisibility(View.GONE);
				acc_y.setVisibility(View.GONE);
				acc_z.setVisibility(View.GONE);
				if (accThread != null) {
					accThread.interrupt();
					accThread = null;
				}
				
				sensorManager.unregisterListener(sensorListener);
				
				isRunning = false;
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){

				DbManager databaseManager = new DbManager(sdContext);
				databaseManager.removeSession(currentSession.getId());
				//databaseManager.removeSession(-1);
				//adapter.notifyDataSetChanged();	

				Intent myIntent = new Intent(v.getContext(), MainActivity.class);
				startActivity(myIntent);
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				btnEdit.setVisibility(View.GONE);
				btnDone.setVisibility(View.VISIBLE);
				newSessionName.setEnabled(true);
			}
		});

		btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				btnDone.setVisibility(View.GONE);
				btnEdit.setVisibility(View.VISIBLE);
				newSessionName.setEnabled(false);
				String name = newSessionName.getText().toString();
				if(!name.equals(""))
				{
					currentSession.setName(name);
					DbManager databaseManager = new DbManager(v.getContext());
					databaseManager.updateSession(currentSession);
					currentSessionName.setText(currentSession.getName());
				}
				else
					Toast.makeText(v.getContext(), activity.getString(R.string.errorEmptyName), Toast.LENGTH_SHORT).show();


			}
		});

		Utilities.setThumbnail(image, currentSession.getBgColor(), currentSession.getImgColor());

		Date sessionDate = new Date(currentSession.getStart());

		sessionStartDate.setText(Utilities.getDate(sessionDate));
		currentSessionName.setText(currentSession.getName());
		newSessionName.setText(currentSession.getName());

	}
	

	@Override
	protected void onResume(){
		super.onResume();
		if(isRunning)
		{
			if(sensorManager == null)
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			if(accThread == null)
			{
				accThread = new Thread(new AccRunner(acc_x, acc_y, acc_z));
				accThread.start();
			}
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(isRunning)
			sensorManager.unregisterListener(sensorListener);
		if (accThread != null) {
			accThread.interrupt();
			accThread = null;
		}
	}


	public void onItemClick(int position, View v) {
		/*Object clickedObj = parent.getItemAtPosition(position);
		Log.i("[onItemClick]", clickedObj.toString());*/

		final Fall fl = (Fall) fallList.get(position);
		Intent myIntent = new Intent(v.getContext(), FallDetails.class);

		myIntent.putExtra(FALL, fl);
		startActivity(myIntent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.		
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			if(sensorManager != null)
				sensorManager.unregisterListener(sensorListener);
			
			if(isRunning)
			{
				accThread.interrupt();
				accThread = null;
				isRunning = false;
			}	
				
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}




	public void setListData(long sID) {

		DbManager databaseManager = new DbManager(sdContext);

		//ok
		//databaseManager.updateDB(); //uncomment this line when update database


		databaseManager.getFalls(sID);

		Cursor c = databaseManager.getFalls(sID);

		while(c.moveToNext()){
            
			//federico 
			final Fall temp = new Fall();

			temp.setId(c.getInt(FallTable.ID));
			temp.setLocation(c.getString(FallTable.LOCATION));
			temp.setDatef(c.getLong(FallTable.DATE)); 

			//test to see if the sessionId is correct
			//System.out.println("LA SESSION ID DEL FALL E' : " + sID);
			

			temp.setSessionID(sID);

			fallList.add(temp);		
		}
	}


	/**
	 * 
	 * This class implements the Runnable class to manage the 
	 * Accelerator with a thread that updates the accelerator data
	 * 
	 * @author thomasgagliardi
	 *
	 */
	private class AccRunner implements Runnable {

		TextView acc_x;
		TextView acc_y;
		TextView acc_z;

		public AccRunner(TextView acc_x, TextView acc_y, TextView acc_z) {
			this.acc_x = acc_x;
			this.acc_y = acc_y;
			this.acc_z = acc_z;

		}

		@Override
		public void run() {
			isRunning = true;
			while (isRunning) {
				activity.runOnUiThread( new Runnable() {

					public void run() {

						double x = round(ax,2);
						acc_x.setText("X = " + x);
						double y = round(ay,2);
						acc_y.setText("Y = " + y);
						double z = round(az,2);
						acc_z.setText("Z = " + z);
					}
				});
			}// is isRunning
			try {

				Thread.sleep(100); // update 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}//end AccRunner

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	private SensorEventListener sensorListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@SuppressLint("ParserError")
		@Override
		public void onSensorChanged(SensorEvent event) {
			//double ax, ay, az;

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				ax = event.values[0];
				ay = event.values[1];
				az = event.values[2];
			}
		}
	};

}
