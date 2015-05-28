package unipd.dei.ESP1415.falldetector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

public class FallService extends Service {

	private static String LOG_TAG = "BoundService";
	private IBinder mBinder = new MyBinder();
	private Chronometer mChronometer;
	private long elapsedMillis;
	private boolean isRunning;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOG_TAG, "in onCreate");
		mChronometer = new Chronometer(this);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();
		isRunning = true;
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

}
