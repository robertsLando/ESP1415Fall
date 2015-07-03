package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Date;

import unipd.dei.ESP1415.falldetector.FallService.MyBinder;
import unipd.dei.ESP1415.falldetector.FallService.aData;
//import unipd.dei.ESP1415.falldetector.GraphViewer.AccRunner;
//import unipd.dei.ESP1415.falldetector.GraphViewer.MyServiceConnection;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
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
	private Context cont;
	private aData data;
	private FallService mService; // the instance of the service
	public boolean mBound = false;
	private boolean isRunning = false;
	private Thread accThread;
	private float wX[] = new float[20];
	private float wY[] = new float[20];
	private float wZ[] = new float[20];
	String[] verlabels = new String[] { "10", "0", "-10" };
	public static final String ACCSERVICE = "accservice";


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
		final TextView acc_x = (TextView) findViewById(R.id.accelerometer_data_x);
		final TextView acc_y = (TextView) findViewById(R.id.accelerometer_data_y);
		final TextView acc_z = (TextView) findViewById(R.id.accelerometer_data_z);

		btnGraph.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				
				btnGraph.setVisibility(v.GONE);
				btnGraphON.setVisibility(v.VISIBLE);
				if(mBound)
				{
					isRunning = true;
					accThread = new Thread(new AccRunner(acc_x, acc_y, acc_z));
					accThread.start();        	
				}
				acc_x.setVisibility(v.VISIBLE);
				acc_y.setVisibility(v.VISIBLE);
				acc_z.setVisibility(v.VISIBLE);
			}
		});

		btnGraphON.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				
				btnGraph.setVisibility(v.VISIBLE);
				btnGraphON.setVisibility(v.GONE);
				if(mBound)
				{
					isRunning = false;
					accThread.interrupt();        	
				}
				acc_x.setVisibility(v.GONE);
				acc_y.setVisibility(v.GONE);
				acc_z.setVisibility(v.GONE);
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
	protected void onStart(){
		super.onStart();
		//*********************BIND TO THE SERVICE*********************/
		Intent intent = new Intent(this, FallService.class);
		intent.putExtra(ACCSERVICE, true);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);		
		//*************************END SERVICE*************************//
	}

	@Override
	protected void onStop(){
		super.onStop();
		// Unbind from the service
		if (mBound) {
			isRunning = false;
			unbindService(mConnection);
			mBound = false;
			if (accThread != null) {
				accThread.interrupt();
				accThread = null;
			}
		}
	}

	@Override
	protected void onPause(){
		super.onPause();
		// Unbind from the service
		if (mBound) {
			isRunning = false;
			unbindService(mConnection);
			mBound = false;
			if (accThread != null) {
				accThread.interrupt();
				accThread = null;
			}
		}
	}

	
	@Override
	protected void onDestroy(){
		super.onStop();
		// Unbind from the service
		if (mBound) {
			isRunning = false;
			unbindService(mConnection);
			mBound = false;
			if (accThread != null) {
				accThread.interrupt();
				accThread = null;
			}
		}
	}
	/**@Override
	protected void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putBoolean("threadRunning", isRunning);
		super.onSaveInstanceState(savedInstanceState); 
	}*/

	public void onItemClick(int position, View v) {
		/*Object clickedObj = parent.getItemAtPosition(position);
		Log.i("[onItemClick]", clickedObj.toString());*/

		final Fall fl = (Fall) fallList.get(position);
		Intent myIntent = new Intent(v.getContext(), FallEvent.class);

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

			final Fall temp = new Fall();

			temp.setId(c.getInt(FallTable.ID));
			temp.setLocation(c.getString(FallTable.LOCATION));
			temp.setDatef(c.getLong(FallTable.DATE)); 
			//test
			System.out.println("LA SESSION ID DEL FALL E' : " + sID);
			temp.setSessionID(sID);

			fallList.add(temp);		
		}

	}


	//SERVICE
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
				IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			MyBinder binder = (MyBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	private void addWindowsData(aData a)
	{
		int i;
		for(i = (wX.length - 1); i>1; i--){
			wX[i-1] = wX[i];
			wY[i-1] = wY[i];
			wZ[i-1] = wZ[i];
		}
		wX[i]= (float)a.x;
		wY[i] = (float)a.y;
		wZ[i] = (float)a.z;

	}

	/**
	 * This class implements the Runnable class to manage the Accelerator of the
	 * FallService with a thread that updates the accelerator data array
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

			while (isRunning) {
				if(mBound) {
					activity.runOnUiThread( new Runnable() {

						@SuppressLint("ShowToast") @Override
						public void run() {
							// TODO Auto-generated method stub
							data = mService.getAData();
							/*SeekBar acc_x = (SeekBar) findViewById(R.id.accelerometer_data_x);
							int x = (int) Math.round(data.x);
							acc_x.setMax(50);
							acc_x.incrementProgressBy(x%50);*/
							double x = round(data.x,2);
							acc_x.setText("X = " + x);
							double y = round(data.y,2);
							acc_y.setText("Y = " + y);
							double z = round(data.z,2);
							acc_z.setText("Z = " + z);
							//							addWindowsData(data);
							//							GraphView graphView = new GraphView(sdContext, wX,
							//									wY, wZ, "Session Graph", verlabels,
							//									GraphView.LINE);
							//							scroll.addView(graphView);
							// setContentView(graphView);
						}
					});
				}

			}// if bound
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

}
