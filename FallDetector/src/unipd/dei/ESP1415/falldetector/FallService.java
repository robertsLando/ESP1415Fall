package unipd.dei.ESP1415.falldetector;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class FallService extends Service implements SensorEventListener {

	private static String LOG_TAG = "BoundService";
	private IBinder mBinder = new MyBinder();

	private long elapsedMillis;
	private long pauseTime;
	private long startTime;
	private boolean isRunning;
	private static boolean isCreated = false;
	private Thread chronoThread;

	// thomasgagliardi
	public double ax, ay, az;
	public double a_norm;
	public int i = 0;
	static int BUFF_SIZE = 50;
	static public double[] window = new double[BUFF_SIZE];
	double sigma = 0.5, th = 10, th1 = 5, th2 = 2;
	private SensorManager sensorManager;
	public static String fall_state, post_state;
	public static Context fsContext;
	public static final String FALL = "fall";
	public LocationManager locationManager;
	public Location mLocation;
	public double longitude;
	public double latitude;
	
	/**
	 * Thomas Gagliardi
	 */
	//implementation on LocationListener interface
	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	    	longitude = location.getLongitude();
	        latitude = location.getLatitude();
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };


	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Fall service onCreate");

		startTime = SystemClock.uptimeMillis();
		pauseTime = 0;

		isCreated = true;

		// thomasgagliardi
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		initialize();
		
		//initialize the GPS
		locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Fall service OnStartCommand received start id " + startId + ": " + intent);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {

		long sessionElapsed = intent
				.getLongExtra(SessionViewAdapter.ELAPSED, 0);

		if(!isRunning)
			setTime(sessionElapsed);

		start(); // starts the chrono thread

		System.out.println("Fall service onBind");

		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		System.out.println("Fall service onUnbind");
		return true;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		isRunning = false;
		isCreated = false;
		locationManager.removeUpdates(locationListener);
		System.out.println("Fall service destroyed");
	}

	public long getTimestamp() {
		return elapsedMillis;
	}

	public void setTime(long timestamp) {
		startTime -= timestamp;
	}

	public void pause() {
		isRunning = false;
		chronoThread.interrupt();
		chronoThread = null;
	}

	public void resume() {
		if (!isRunning) {
			pauseTime = SystemClock.uptimeMillis()
					- (startTime + elapsedMillis);
			start();
		}
	}

	public void start() {
		isRunning = true;
		chronoThread = new Thread(new MyChrono());
		chronoThread.start();
	}


	public static boolean isCreated() {
		return isCreated;
	}


	public class MyBinder extends Binder {
		FallService getService() {
			return FallService.this;
		}
	}

	private class MyChrono implements Runnable {

		@Override
		public void run() {
			while (isRunning) {
				elapsedMillis = SystemClock.uptimeMillis() - startTime
						- pauseTime;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("Chrono Thread has been interrupted");
				}
			}

		}// run()

	} // MyChrono Class

	/**
	 * thomas gagliardi
	 */

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@SuppressLint("ParserError")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			ax = event.values[0];
			ay = event.values[1];
			az = event.values[2];
			AddData(ax, ay, az);
			posture_recognition(window, ay);
			fall_recognition(window);
			SystemState(fall_state, post_state);
			if (!fall_state.equalsIgnoreCase(post_state)) {
				fall_state = post_state;
			}
		}
	}

	private void initialize() {
		// TODO Auto-generated method stub
		for (i = 0; i < BUFF_SIZE; i++) {
			window[i] = 0;
		}
		fall_state = "none";
		post_state = "none";
	}

	private void fall_recognition(double[] window1) {
		int l = window1.length;
		int f = 0;
		double tMin = 100, tMax = 0;

		for (int i = 0; i < l; i++)
			if (window[i] > tMax)
				tMax = window[i];

		for (int i = 0; i < l; i++)
			if (window[i] < tMin)
				tMin = window[i];

		if ((tMax - tMin) > (2 * 9.81))
			fall_state = "fall";
		else
			fall_state = "none";
	}

	private void posture_recognition(double[] window2, double ay2) {
		// TODO Auto-generated method stub
		int zrc = compute_zrc(window2);
		if (zrc == 0) {

			if (Math.abs(ay2) < th1) {
				post_state = "sitting";
			} else {
				post_state = "standing";
			}

		} else {

			if (zrc > th2) {
				post_state = "walking";
			} else {
				post_state = "none";
			}
		}
	}

	private int compute_zrc(double[] window2) {
		// TODO Auto-generated method stub
		int count = 0;
		for (i = 1; i <= BUFF_SIZE - 1; i++) {

			if ((window2[i] - th) < sigma && (window2[i - 1] - th) > sigma) {
				count = count + 1;
			}
		}
		return count;
	}

	private void SystemState(String fall_state1, String post_state1) {
		// TODO Auto-generated method stub

		// Fall !!
		if (!fall_state1.equalsIgnoreCase(post_state1)) {
			if (fall_state1.equalsIgnoreCase("fall")
					|| fall_state1.equalsIgnoreCase("none")) {
				if (post_state1.equalsIgnoreCase("none")) {
					//reach the data
					latitude = mLocation.getLatitude();
					longitude = mLocation.getLongitude();
					// call SendEmail					
					Fall fl = createFall();
					Intent myIntent = new Intent(this,SendEmail.class);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	//to call Calling startActivity() from outside of an Activity 
					myIntent.putExtra(FALL, fl);
					startActivity(myIntent);
				}
			}
		}
	}

	private void AddData(double ax2, double ay2, double az2) {
		// TODO Auto-generated method stub
		a_norm = Math.sqrt(ax * ax + ay * ay + az * az);
		for (i = 0; i <= BUFF_SIZE - 2; i++) {
			window[i] = window[i + 1];
		}
		window[BUFF_SIZE - 1] = a_norm;

	} 

	private Fall createFall() {
		Fall temp = new Fall();
		/*
		 * String name = text.getText().toString();
		 * 
		 * if (!name.equals("")) { temp.setName(name);
		 * temp.setBgColor(color[0]); temp.setImgColor(color[1]);
		 * temp.setStart(0);
		 * 
		 * DbManager databaseManager = new DbManager(fsContext);
		 * 
		 * //long id = databaseManager.addFall(temp); NEED TO CREATE THE METHOD
		 * }
		 */
		return temp;
	}
}
