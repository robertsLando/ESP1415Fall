package unipd.dei.ESP1415.falldetector;


import unipd.dei.ESP1415.falldetector.FallService.MyBinder;
import unipd.dei.ESP1415.falldetector.FallService.aData;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;


public class GraphViewer extends Activity {

	private Context cont = this.getBaseContext();
	private aData data;
	private FallService mService; // the instance of the service
	public boolean mBound = false;
	private boolean isRunning = false;
	private Activity activity;
	private Thread accThread;
	private float wX[] = new float[20];
	private float wY[] = new float[20];
	private float wZ[] = new float[20];
	String[] verlabels = new String[] { "10", "0", "-10" };
	public static final String ACCSERVICE = "accservice";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//Intent myIntent = getIntent();

		super.onCreate(savedInstanceState);
		//float[] values = new float[] {-2.0f,4.5f,-3.0f,5.0f,1.5f,-2.0f,4.5f,-3.0f,5.0f,1.5f,-2.0f,4.5f,-3.0f,5.0f,1.5f,-2.0f,4.5f,-3.0f,5.0f,1.5f,-2.0f,4.5f,-3.0f,5.0f,1.5f};
		//String[] horlabels = new String[] { "today", "tomorrow", "next week", "next month" };
		//GraphView graphView = new GraphView(this, values, "Session Graph",verlabels, GraphView.LINE);
		//setContentView(graphView);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//*********************BIND TO THE SERVICE*********************//
		Intent intent = new Intent(this, FallService.class);
		intent.putExtra(ACCSERVICE, true);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);		
		//*************************END SERVICE*************************//
		if(mBound)
		{
			isRunning = true;
			accThread = new Thread(new AccRunner());
			accThread.start();        	
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mBound) {
			isRunning = false;
			unbindService(mConnection);
			mBound = false;
			accThread.interrupt();
			accThread = null;
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

			@Override
			public void run() {
				while (isRunning) {
					if(mBound) {
						data = mService.getAData();
						addWindowsData(data);
						GraphView graphView = new GraphView(cont, wX, wY, wZ, "Session Graph",verlabels, GraphView.LINE);
						setContentView(graphView);

					}// if bound
					try {

						Thread.sleep(500); // update every second
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}//end AccRunner


}

