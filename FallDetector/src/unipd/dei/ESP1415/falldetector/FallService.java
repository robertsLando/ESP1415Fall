package unipd.dei.ESP1415.falldetector;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

public class FallService extends Service implements SensorEventListener {

	private static String LOG_TAG = "BoundService";
	private IBinder mBinder = new MyBinder();
	private Chronometer mChronometer;
	private long elapsedMillis;
	private boolean isRunning;

	//thomasgagliardi
	public double ax,ay,az;
	public double a_norm;
	public int i=0;
	static int BUFF_SIZE=50;
	static public double[] window = new double[BUFF_SIZE];
	double sigma=0.5,th=10,th1=5,th2=2;
	private SensorManager sensorManager;
	public static String fall_state,post_state;
	public static Context fsContext;
	public static final String FALL = "fall";


	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOG_TAG, "in onCreate");
		mChronometer = new Chronometer(this);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();

		isRunning = true;

		//thomasgagliardi
		sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
		initialize();

	}

	@Override
	public IBinder onBind(Intent intent) {
		
		long sessionElapsed = intent.getLongExtra(SessionViewAdapter.ELAPSED, 0);
		
		if(isRunning) //The service has just been created
			setTime(sessionElapsed);
		else
			setTime(getTimestamp());
		
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
		mChronometer.stop();
		isRunning = false;
		System.out.println("Fall service destroyed");
	}

	public long getTimestamp() {
		elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
		return elapsedMillis;
	}

	public void setTime(long timestamp) {
		elapsedMillis = timestamp;
		mChronometer.setBase(mChronometer.getBase() - timestamp);
	}

	public void pause() {
		elapsedMillis = mChronometer.getBase() - SystemClock.elapsedRealtime() ;
		mChronometer.stop();
		isRunning = false;
	}

	public void resume() {
		mChronometer.setBase(SystemClock.elapsedRealtime() + elapsedMillis);
		mChronometer.start();
		isRunning = true;
	}

	public class MyBinder extends Binder {
		FallService getService() {
			return FallService.this;
		}
	}

	/**
	 * thomas gagliardi
	 */

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@SuppressLint("ParserError")
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			ax=event.values[0];
			ay=event.values[1];
			az=event.values[2];
			AddData(ax,ay,az);
			posture_recognition(window,ay);
			fall_recognition(window);
			SystemState(fall_state,post_state);	
			if(!fall_state.equalsIgnoreCase(post_state)){
				fall_state=post_state;
			}
		}
	}

	private void initialize() {
		// TODO Auto-generated method stub
		for(i=0;i<BUFF_SIZE;i++){
			window[i]=0;
		}
		fall_state="none";
		post_state="none";
	}

	private void fall_recognition(double[] window1)
	{
		int l = window1.length;
		int f = 0;
		double tMin = 100, tMax = 0;

		for(int i = 0; i<l; i++)
			if(window[i] > tMax)
				tMax = window[i];

		for(int i = 0; i<l; i++)
			if(window[i] < tMin)
				tMin = window[i];

		if((tMax - tMin) > (2 * 9.81))
			fall_state = "fall";
		else
			fall_state = "none";
	}

	private void posture_recognition(double[] window2,double ay2) {
		// TODO Auto-generated method stub
		int zrc=compute_zrc(window2);
		if(zrc==0){

			if(Math.abs(ay2)<th1){
				post_state="sitting";
			}else{
				post_state="standing";
			}

		}else{

			if(zrc>th2){
				post_state="walking";
			}else{
				post_state="none";
			}
		}
	}

	private int compute_zrc(double[] window2) {
		// TODO Auto-generated method stub
		int count=0;
		for(i=1;i<=BUFF_SIZE-1;i++){

			if((window2[i]-th)<sigma && (window2[i-1]-th)>sigma){
				count=count+1;
			}
		}
		return count;
	}

	private void SystemState(String fall_state1,String post_state1) {
		// TODO Auto-generated method stub

		//Fall !!
		if(!fall_state1.equalsIgnoreCase(post_state1)){
			if(fall_state1.equalsIgnoreCase("fall") || fall_state1.equalsIgnoreCase("none")){
				if(post_state1.equalsIgnoreCase("none")){
					//call SendEmail
					
					Fall fl = createFall();
					Intent myIntent = new Intent(fsContext, SessionDetails.class);
					myIntent.putExtra(FALL, fl);
					startActivity(myIntent);
				}
			}
		}
	}

	private void AddData(double ax2, double ay2, double az2) {
		// TODO Auto-generated method stub
		a_norm=Math.sqrt(ax*ax+ay*ay+az*az);
		for(i=0;i<=BUFF_SIZE-2;i++){
			window[i]=window[i+1];
		}
		window[BUFF_SIZE-1]=a_norm;

	}

	private Fall createFall(){
		Fall temp = new Fall();
		/*
		String name = text.getText().toString();
		
		if (!name.equals("")) {
			temp.setName(name);
			temp.setBgColor(color[0]);
			temp.setImgColor(color[1]);
			temp.setStart(0);

			DbManager databaseManager = new DbManager(fsContext);

			//long id = databaseManager.addFall(temp);  NEED TO CREATE THE METHOD
		}*/
		return temp;
	}
}
