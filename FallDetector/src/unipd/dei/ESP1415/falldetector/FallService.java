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

	public String getTimestamp() {
		elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
		int hours = (int) (elapsedMillis / 3600000);
		int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
		int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
		int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);
		return hours + ":" + minutes + ":" + seconds + ":" + millis;
	}

	public void pause() {
		elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
	}

	public void resume() {
		mChronometer.setBase(mChronometer.getBase() + elapsedMillis);
	}

	public class MyBinder extends Binder {
		FallService getService() {
			return FallService.this;
		}
	}

}
