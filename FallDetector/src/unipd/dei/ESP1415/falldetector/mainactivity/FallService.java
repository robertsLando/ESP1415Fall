
package unipd.dei.ESP1415.falldetector.mainactivity;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.falldetailsactivity.FallData;
import unipd.dei.ESP1415.falldetector.sessiondetails.Fall;
import unipd.dei.ESP1415.falldetector.utilities.Utilities;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class FallService extends Service {

	// service binder
	private IBinder mBinder = new MyBinder();

	private long elapsedMillis; 
	private long pauseTime; //the time that the chrono has been in pause
	private long startTime; //the time when the chrono has been started
	private boolean isRunning; // checks  if the chrono is running
	private static boolean isCreated = false; //checks if the service has been created
	private Thread chronoThread; //the thread that updates the elapsedmillis
	private static long sessionID; //the id of the associated running session

	//Intent constraint
	public final static String FALL = "fall"; 
	
	private double a_norm; //the value of the acceleration sqrt(x^2+y^2+z^2)
	private static final int TWO_MINUTES = 1000 * 60 * 2; // location update
															// time
	private int i = 0; //the index in the buffer windows for accelerometer data
	private boolean fixed = false; //checks if the position has been fixed or not
	private static boolean detected = false; //checks if a fall has been detected
	private int BUFF_SIZE = 50; //the buffer size for accelerometer data
	private FallData[] window = new FallData[BUFF_SIZE]; //the buffer for accelerometer data
	private final double sigma = 0.5, th = 10, th1 = 5, th2 = 2; //constraints for fall recognition
	private SensorManager sensorManager; //the sensor manager
	private String fall_state, post_state; //the state of fall
	private LocationManager locationManager; //the location manager
	private Location mLocation; //last known location
	private Thread fallDetected = new Thread(new FallRecognizedThread()); //thread that save and send email
	private Thread locationThread = new Thread(new FindLocationThread()); //updates the location
	private double ax, ay, az; //acceleration components x y z
	
	private int mode; //I use this variable to define the rate of the accelerometer---->the user choose it in the preference
	private long maxSessionDuration; //I use this variable to define the maximum duration
	
	//the accelerometer listener
	private SensorEventListener sensorListener = new SensorEventListener() {
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@SuppressLint("ParserError")
		@Override
		public void onSensorChanged(SensorEvent event) {
			//double ax, ay, az;

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				//get accelerometer datas
				ax = event.values[0];
				ay = event.values[1];
				az = event.values[2];
				
				//add the data in the buffer
				AddData(ax, ay, az);
				
				//compute the data
				posture_recognition(window, ay);
				fall_recognition(window);
				SystemState(fall_state, post_state);
				
				if (!fall_state.equalsIgnoreCase(post_state)) {
					fall_state = post_state;
				}
			}
		}
	};
	
	//location listener
	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (isBetterLocation(location, mLocation))
				mLocation = location;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Fall service onCreate");

		startTime = SystemClock.uptimeMillis();
		pauseTime = 0;
		sessionID = -1;

		isCreated = true;
		mode = getRate();

		fallDetected.setName("Fall Detect Thread");
		locationThread.setName("Find location Thread");

		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);

		locationThread.start();
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(sensorListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				mode);
		
	   
	   initialize();
	   
	   //test to see if the mode is the mode the user selected
	   //System.out.println("IL MODE SCELTO IN ONCREATE E' " + mode);
	    

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Fall service OnStartCommand received start id "
				+ startId + ": " + intent);
		
		//get the id of the associated session
		if(intent != null)
			sessionID = intent.getLongExtra(SessionViewAdapter.ID, -1);
		
		if(sessionID == -1 || sessionID == 0)
		{
			//if something gets wrong, get the id from the database
			DbManager databaseManager = new DbManager(getApplicationContext());
			sessionID = databaseManager.getRunningSessionID();
		}
		
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		long sessionElapsed = intent
				.getLongExtra(SessionViewAdapter.ELAPSED, 0);

		if (!isRunning)
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
		//free resources
		locationManager.removeUpdates(locationListener);
		sensorManager.unregisterListener(sensorListener);
		System.out.println("Fall service destroyed");
	}
	
	/*public aData getAData(){
		accData.setX(ax);
		accData.setY(ay);
		accData.setZ(az);
		return accData;
	}*/

	
	/**
	 * Used from bounded activities to get the time elapsed
	 * @return the elapsed time in millisecond
	 */
	public long getTimestamp() {
		return elapsedMillis;
	}

	/**
	 * Set the chrono start time
	 * @param timestamp
	 */
	public void setTime(long timestamp) {
		startTime -= timestamp;
	}

	/**
	 * Pause the chrono
	 */
	public void pause() {
		isRunning = false;
		chronoThread.interrupt();
		chronoThread = null;
		locationManager.removeUpdates(locationListener);
		locationThread.interrupt();
		locationThread = null;
		sensorManager.unregisterListener(sensorListener);
	}

	/**
	 * Resume the chrono
	 */
	public void resume() {
		if (!isRunning) {
			pauseTime = SystemClock.uptimeMillis()
					- (startTime + elapsedMillis);
			start();
			
			//gets the rate from the shared preferences
			mode = getRate();
			// System.out.println("IL MODE SCELTO IN RESUME E' " + mode);
			
			sensorManager.registerListener(sensorListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					mode);
			locationThread = new Thread(new FindLocationThread());
			locationThread.start();
			
		}
	}

	/**
	 * Starts the chrono
	 */
	public void start() {
		isRunning = true;
		chronoThread = new Thread(new MyChrono());
		chronoThread.start();
		chronoThread.setName("Fall service Chrono thread");
	}

	public static boolean isCreated() {
		return isCreated;
	}

	public class MyBinder extends Binder {
		FallService getService() {
			return FallService.this;
		}
	}

	/**
	 * This class handle the chrono and update time elapsed
	 * @author daniellando
	 *
	 */
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

/**
 * Initialize the buffer
 */
	private void initialize() {

		for (i = 0; i < BUFF_SIZE; i++) {
			window[i] = new FallData();
		}
		fall_state = "none";
		post_state = "none";
	}

	/**
	 * Compute data
	 * @param window1 the buffer
	 */
	private void fall_recognition(FallData[] window1) {
		int l = window1.length;
//		int f = 0;
		double tMin = 100, tMax = 0;

		for (int i = 0; i < l; i++)
			if (window[i].getAccelerationY() > tMax)
				tMax = window[i].getAccelerationY();

		for (int i = 0; i < l; i++)
			if (window[i].getAccelerationY() < tMin)
				tMin = window[i].getAccelerationY();

		if ((tMax - tMin) > (2 * SensorManager.GRAVITY_EARTH)) // if the
																// difference
																// between the
																// max-min
																// acceleration
																// values is
																// greater then
																// g*2 a fall is
																// detected
			fall_state = "fall";
		else
			fall_state = "none";
	}

	/**
	 * Recognize the posture
	 * @param window2 the buffer
	 * @param ay2
	 */
	private void posture_recognition(FallData[] window2, double ay2) {

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

	private int compute_zrc(FallData[] window2) {

		int count = 0;
		for (i = 1; i <= BUFF_SIZE - 1; i++) {

			if ((window2[i].getAccelerationY() - th) < sigma
					&& (window2[i - 1].getAccelerationY() - th) > sigma) {
				count = count + 1;
			}
		}
		return count;
	}

	private void SystemState(String fall_state1, String post_state1) {

		// Fall !!
		if (!fall_state1.equalsIgnoreCase(post_state1)) {
			if (fall_state1.equalsIgnoreCase("fall")
					|| fall_state1.equalsIgnoreCase("none")) {
				if (post_state1.equalsIgnoreCase("none")) {
					// reach the data
					if(SendEmail.isSendingEmail == false && detected == false)	
					{
						sensorManager.unregisterListener(sensorListener);
						fallDetected = new Thread(new FallRecognizedThread());
						fallDetected.start();
						detected = true;
					}
				}
			}
		}
	}
	
/**
 * Add sensor accelerometer data in the buffer
 * @param ax2 X component
 * @param ay2 Y component
 * @param az2 Z component
 */
	private void AddData(double ax2, double ay2, double az2) {

		a_norm = Math.sqrt(ax2 * ax2 + ay2 * ay2 + az2 * az2); // acceleration
																// value
		for (i = 0; i <= BUFF_SIZE - 2; i++) { // Add the new fallData ordered
												// by time (ASC)
			window[i].setAccelerationY(window[i + 1].getAccelerationY());
			window[i].setTimeX(window[i + 1].getTimeX());
		}
		window[BUFF_SIZE - 1].setAccelerationY(a_norm);
		window[BUFF_SIZE - 1].setTimeX(System.currentTimeMillis());

	}

	/**
	 * Create a fall instance, save it and all data in the windows buffer in the database
	 * and reinitialize the buffer for new data.
	 * @return
	 */
	private Fall createFall() {
		Fall temp = new Fall();

		String location = "-------";
		temp.setDatef(System.currentTimeMillis());
		if (mLocation != null)
			location = getAddress();
			
		temp.setLocation(location);
		temp.setSessionID(sessionID);

		DbManager databaseManager = new DbManager(getApplicationContext());

		long id = databaseManager.addFall(temp);

		if(id == -1)
			System.out.println("Error inserting Fall in the Database");
		
		temp.setId(id);

		databaseManager.addFallData(window,id);
		
		
		initialize(); //prepare for a new fall recognition

		return temp;
	}

	/**
	 * This thread starts when a fall has been recognized. It create a fall instance
	 * and save it and all his data in the window buffer in the database, finally it 
	 * starts the email activity
	 * @author daniellando
	 *
	 */
	private class FallRecognizedThread implements Runnable {

		@Override
		public void run() {

			// call SendEmail
			Fall fl = createFall();
			Intent myIntent = new Intent(getApplicationContext(), SendEmail.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // to call Calling
																// startActivity()
																// from outside
																// of an
																// Activity
			myIntent.putExtra(FALL, fl);
			
			detected = false;
			
			/*//federico---->use the rate that the user will
			SharedPreferences settings = 
			        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			String rate = settings.getString("accelerometer_settings", "3");
		    //System.out.println(rate); test
		    mode = Integer.parseInt(rate);*/
			 mode = getRate();
			 //System.out.println("IL MODE SCELTO I RUN E' " + mode);
			 
			sensorManager.registerListener(sensorListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					mode);
			
			startActivity(myIntent);
			
		}// run()

	} // FallRecognizedThread
	
	
	/**
	 * This thread fix the location every 2 minutes
	 * @author daniellando
	 *
	 */
	private class FindLocationThread implements Runnable {

		@Override
		public void run() {
			
			while(true)
			{
				LocationHandler locationHandler = new LocationHandler();
				locationHandler.start();
				fixed = false;
				long lastLocationTime = 0;
	
				if (mLocation != null)
					lastLocationTime = mLocation.getTime();
	
				while (fixed == false) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
	
						e.printStackTrace();
					}
	
					if (mLocation != null)
						if (lastLocationTime < mLocation.getTime())
							fixed = true;
				}// end while
				
			
				locationManager.removeUpdates(locationListener);
				locationHandler.interrupt();
				locationHandler = null;
				
				try {
					Thread.sleep(TWO_MINUTES);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			

		}// run()

	} // findLocationThread
	
	/**
	 * This thread handles location updates
	 * @author daniellando
	 *
	 */
	private class LocationHandler extends Thread {

	      @Override
	      public void run() {
	    	  Looper.prepare();
				
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, locationListener);
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
				
				Looper.loop();
				
	      }
	  }

	/**
	 * This method estimate the location
	 * 
	 * @param location
	 * @param currentBestLocation
	 * @return true if location is better than currentBestLocation
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * This method is used to check if provider1 is the same of provide2
	 * 
	 * @param provider1
	 * @param provider2
	 * @return true if prvider1 == provider2
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Retrieve the address from location
	 * 
	 * @return The address at the given location in String format
	 */
	private String getAddress() {
		String address = "none";
		try {

			Geocoder geo = new Geocoder(this.getApplicationContext(),
					Locale.getDefault());
			List<Address> addresses = geo.getFromLocation(
					mLocation.getLatitude(), mLocation.getLongitude(), 1);
			if (addresses.size() > 0) {
				address = addresses.get(0).getAddressLine(0) + ", "
						+ addresses.get(0).getLocality() + " "
						+ addresses.get(0).getPostalCode() + ", "						
						+ addresses.get(0).getCountryName() + "\n"
						+ "Latitude: " + Utilities.latitudeLongitudeToString(addresses.get(0).getLatitude()) + "\n" 
						+ "Longitude: " + Utilities.latitudeLongitudeToString(addresses.get(0).getLongitude()) + "\n" 
						+ "Location Time: " + Utilities.getOnlyTime(new Date(mLocation.getTime()));
			}

		} catch (Exception e) {
			System.out.println("Something went wrong with getAddress method");
			if(mLocation != null)
			address = "Latitude: " + Utilities.latitudeLongitudeToString(mLocation.getLatitude()) + " Longitude: " + Utilities.latitudeLongitudeToString(mLocation.getLongitude());
		}

		return address;
	}
	
	/**
	 * This method is used to read the selected item in the preference for the rate
	 * 
 	 * @return the value of accelerometer's rate
	 */
	private int getRate(){
		//federico---->use the rate that the user will
		SharedPreferences settings = 
		        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String rate = settings.getString("accelerometer_settings", "3");
	    //System.out.println(rate); test
	    int rateInt = Integer.parseInt(rate);
	    return rateInt;
     
	}
	/**
	 * This method is used to find the selected item for the maximum duration of the session
	 * 
	 * @return the value of the maximum duration
	 */
	private long getDuration() {
		SharedPreferences settings = 
		        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String duration = settings.getString("session_duration", "3600" );
		long durationLong = Long.parseLong(duration);
		return durationLong;
	}

}
