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
	long elapsedMillis;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOG_TAG, "in onCreate");
		mChronometer = new Chronometer(this);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();
		elapsedMillis = 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(LOG_TAG, "in onBind");
		return mBinder;
	}

	@Override
	public void onRebind(Intent intent) {
		Log.v(LOG_TAG, "in onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(LOG_TAG, "in onUnbind");
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(LOG_TAG, "in onDestroy");
		mChronometer.stop();
	}

	public long getTimestamp() {
		elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
		return elapsedMillis;
	}

	public void pause() {
		elapsedMillis = mChronometer.getBase() -SystemClock.elapsedRealtime() ;
		mChronometer.stop();
	}

	public void resume() {
		mChronometer.setBase(SystemClock.elapsedRealtime() + elapsedMillis);
		mChronometer.start();
	}

	public class MyBinder extends Binder {
		FallService getService() {
			return FallService.this;
		}
	}

}
