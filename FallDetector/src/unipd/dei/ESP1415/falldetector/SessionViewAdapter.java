package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Date;

import unipd.dei.ESP1415.falldetector.FallService.MyBinder;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The adapter for the list view
 * 
 * @author daniellando
 *
 */
public class SessionViewAdapter extends BaseAdapter implements OnClickListener {

	private Activity activity; // the activity where the ListView is placed
	public ArrayList<Session> sessionList; // the container
	private static LayoutInflater inflater = null; // calls external xml layout
	// ()
	private SessionViewHolder itemVisible = null;
	public SessionViewHolder itemRunning = null;
	private SessionViewAdapter adapter;
	public static final String SESSION = "session";
	private FallService mBoundService; // the instance of the service
	public boolean mServiceBound = false; // bind of service true = service is
											// bounded (mainactivity) false =
											// not buonded
	private Thread chronoThread;
	private boolean mServiceStarted = false;
	private boolean isRunning = false;
	private ServiceConnection mServiceConnection = new ServiceConnection() { // monitoring
																				// the
																				// state
																				// of
																				// an
																				// application
																				// service

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceBound = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder myBinder = (MyBinder) service;
			mBoundService = myBinder.getService();
			mServiceBound = true;
		}
	};

	public SessionViewAdapter(Activity mactivity, ArrayList<Session> data) {

		activity = mactivity;
		sessionList = data;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter = this;

	}

	@Override
	public int getCount() {
		return sessionList.size();
	}

	@Override
	public Object getItem(int position) {
		return sessionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return sessionList.get(position).getId();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getView(final int position, View mySessionView, ViewGroup parent) {

		final SessionViewHolder holder;

		if (mySessionView == null) {

			// set the layout of the item
			mySessionView = inflater.inflate(R.layout.session_child, null);

			// create the item holder
			holder = new SessionViewHolder();
			// Get widgets from the view
			holder.sessionName = (TextView) mySessionView
					.findViewById(R.id.sessionName);
			holder.falls = (TextView) mySessionView
					.findViewById(R.id.fallsNumber);
			holder.startTime = (TextView) mySessionView
					.findViewById(R.id.startTime);
			holder.endTime = (TextView) mySessionView
					.findViewById(R.id.endTime);
			holder.endText = (TextView) mySessionView.findViewById(R.id.end);
			holder.durationTime = (TextView) mySessionView
					.findViewById(R.id.durationTime);

			holder.expandable = (RelativeLayout) mySessionView
					.findViewById(R.id.expandableLayout);
			holder.playButton = (ImageView) mySessionView
					.findViewById(R.id.playButton);
			holder.pauseButton = (ImageView) mySessionView
					.findViewById(R.id.pauseButton);
			holder.fallIcon = (ImageView) mySessionView
					.findViewById(R.id.sessionImage);
			holder.moreButton = (ImageView) mySessionView
					.findViewById(R.id.moreButton);
			holder.durationText = (TextView) mySessionView
					.findViewById(R.id.duration);
			holder.chrono = (TextView) mySessionView
					.findViewById(R.id.chronometer);


			mySessionView.setTag(holder);

		} else
			// the view already exist
			holder = (SessionViewHolder) mySessionView.getTag();

		final Session ses = (Session) sessionList.get(position);

		Date start = new Date(ses.getStart());
		Date end = new Date(ses.getEnd());

		// Set the value of the widgets
		holder.sessionName.setText(ses.getName());
		holder.falls.setText(String.valueOf(ses.getFalls()));

		Utilities.setThumbnail(holder.fallIcon, ses.getBgColor(),
				ses.getImgColor());

		if (ses.getEnd() == 0) { // not ended

			holder.pauseButton.setVisibility(View.GONE);
			holder.chrono.setVisibility(View.VISIBLE);
			holder.chrono.setText(Utilities.getTime(ses.getTimeElapsed()));
			holder.endTime.setVisibility(View.GONE);	
			holder.endText.setVisibility(View.INVISIBLE);
			holder.playButton.setVisibility(View.VISIBLE);
			holder.durationTime.setVisibility(View.GONE);
			holder.durationText.setVisibility(View.GONE);
			holder.expandable.setVisibility(View.VISIBLE);
			itemVisible = holder;
			mySessionView.setBackgroundColor(Color.GREEN);
			
		} else {

			displayDuration(holder, ses);
		}

		if (ses.getStart() == 0) // Hasn't been started yet
		{
			holder.startTime.setText(MainActivity.mContext
					.getString(R.string.toStart));
		} else
			holder.startTime.setText(Utilities.getDate(start));
		

		holder.moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// Creating the instance of PopupMenu
				PopupMenu popup = new PopupMenu(v.getContext(),
						holder.moreButton);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.more_menu,
						popup.getMenu());

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						String selected = (String) item.getTitle();
						DbManager databaseManager = new DbManager(
								MainActivity.mContext);

						if (selected.equals(MainActivity.mContext
								.getString(R.string.delete))) {
							databaseManager.removeSession(ses.getId());
							sessionList.remove(position);
							adapter.notifyDataSetChanged();
							if(ses.getEnd() == 0) //the user have deleted the session to complete
								MainActivity.completeSession(); //show the fab

						}// delete
						if (selected.equals(MainActivity.mContext
								.getString(R.string.rename))) // rename
						{
							// custom dialog
							final Dialog dialog = new Dialog(v.getContext());
							dialog.setContentView(R.layout.new_session_dialog);
							dialog.setTitle(activity
									.getString(R.string.renameSession));

							// set the custom dialog components - text, image
							// and button
							final EditText text = (EditText) dialog
									.findViewById(R.id.newSessionName);
							final ImageView image = (ImageView) dialog
									.findViewById(R.id.newSessionImage);

							Button dialogOkButton = (Button) dialog
									.findViewById(R.id.dialogButtonOK);
							Button dialogCancelButton = (Button) dialog
									.findViewById(R.id.dialogButtonCancel);

							text.setText(ses.getName());
							Utilities.setThumbnail(image, ses.getBgColor(),
									ses.getImgColor());

							// if button is clicked, close the custom dialog

							dialogOkButton
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											String name = text.getText()
													.toString();
											if (!name.equals("")) {
												ses.setName(name);
												DbManager databaseManager = new DbManager(
														activity.getApplicationContext());
												databaseManager
														.updateSession(ses);
												sessionList.get(position)
														.setName(name);
												dialog.dismiss();
											} else
												Toast.makeText(
														v.getContext(),
														activity.getString(R.string.errorEmptyName),
														Toast.LENGTH_SHORT)
														.show();

										}
									});// onCLickOkButton
							dialogCancelButton
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();

										}
									});// OnclickCancelButton
							dialog.show();

						}// rename

						if (selected.equals(MainActivity.mContext
								.getString(R.string.stop))) // stop
						{
							if (mServiceBound) {
								activity.unbindService(mServiceConnection);
								mServiceBound = false;
							}
							Intent intent = new Intent(v.getContext(),
									FallService.class);
							activity.stopService(intent);

							// update end
							databaseManager = new DbManager(v.getContext());
							sessionList.get(position).setEnd(
									System.currentTimeMillis());
							databaseManager.updateEnd(ses.getId());
							displayDuration(holder, ses);
							MainActivity.completeSession();//show fab
							adapter.notifyDataSetChanged();				

						} // stop
						if (selected.equals(MainActivity.mContext
								.getString(R.string.details))) // details
						{

							displayDetails(v.getContext(), ses);

						}// details

						return true;
					}
				}); // popup listener

				if (ses.getEnd() != 0 || ses.getStart() == 0)
					popup.getMenu().getItem(2).setVisible(false); // hide stop
																	// if ended
																	// or not
																	// started
																	// yet
				popup.show(); // showing popup menu
			}
		}); // closing the setOnClickListener method on more button

		holder.playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.pauseButton.setVisibility(View.VISIBLE);
				holder.playButton.setVisibility(View.GONE);

				// the service hasn't been already bounded
				if (!mServiceBound) {

					Intent intent = new Intent(
							activity.getApplicationContext(), FallService.class);

					if (!mServiceStarted) {
						// start the service
						activity.startService(intent);
						mServiceStarted = true;
					}

					// bind the service
					activity.bindService(intent, mServiceConnection,
							Context.BIND_AUTO_CREATE);
					
						
				}

				if (ses.getStart() == 0) {

					// save the start time in the db
					DbManager dbmanager = new DbManager(v.getContext());
					dbmanager.updateStart(ses.getId());
					// update the list item
					ses.setStart(System.currentTimeMillis());
					sessionList.get(position).setStart(ses.getStart());
					holder.startTime.setText(Utilities.getDate(new Date(System
							.currentTimeMillis())));
					

				}// start == 0

				else {
					if (ses.getTimeElapsed() > 0)
						if (mServiceBound)
							mBoundService.resume();
				}

				isRunning = true;
				itemRunning = holder;

				// start the thread that updates the value of the elapsed time
				// every second
				chronoThread = new Thread(new MyRunner(holder));
				
				chronoThread.start();
				
				//update session status in sessionlist and database
				sessionList.get(position).setRunning(true);
				DbManager databaseManager = new DbManager(v.getContext());
				databaseManager.updateStatus(ses.getId(), true);

			}// onClick playButton
		});// OnClickListener playButton

		holder.pauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.playButton.setVisibility(View.VISIBLE);
				holder.pauseButton.setVisibility(View.GONE);

				itemRunning = null;
				isRunning = false;

				if (mServiceBound) {
					long timeelapsed = mBoundService.getTimestamp();
					sessionList.get(position).setTimeElapsed(timeelapsed);
					sessionList.get(position).setRunning(false);
					DbManager databaseManager = new DbManager(v.getContext());
					databaseManager.updateTimeElapsed(ses.getId(), timeelapsed);
					databaseManager.updateStatus(ses.getId(), false);
					mBoundService.pause();
				}

			}
		});

		mySessionView.setOnClickListener(new OnItemClickListener(position));

		return mySessionView;

	}

	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View myView) {

			SessionViewHolder holder = (SessionViewHolder) myView.getTag();
			if (itemVisible == null) {
				holder.expandable.setVisibility(View.VISIBLE);
				itemVisible = holder;
			} else if (itemVisible == holder) {
				displayDetails(myView.getContext(), sessionList.get(mPosition));
				holder.expandable.setVisibility(View.GONE);
				itemVisible = null;
			} else {
				holder.expandable.setVisibility(View.VISIBLE);
				itemVisible.expandable.setVisibility(View.GONE);
				itemVisible = holder;
			}

			MainActivity sct = (MainActivity) activity;
			sct.onItemClick(mPosition);
		}
	}

	private void displayDetails(Context c, Session ses) {
		Intent myIntent = new Intent(c, SessionDetails.class);
		myIntent.putExtra(SESSION, ses);
		activity.startActivity(myIntent);
	}

	private void displayDuration(SessionViewHolder holder, Session ses) {
		holder.chrono.setVisibility(View.GONE);
		holder.endTime.setVisibility(View.VISIBLE);
		holder.endText.setVisibility(View.VISIBLE);
		holder.playButton.setVisibility(View.GONE);
		holder.durationTime.setVisibility(View.VISIBLE);
		holder.durationText.setVisibility(View.VISIBLE);
		holder.endTime.setText(Utilities.getDate(new Date(ses.getEnd())));
		long millis = ses.getEnd() - ses.getStart();

		String duration = Utilities.getTime(millis);
		holder.durationTime.setText(duration);
	}

	private class MyRunner implements Runnable {

		SessionViewHolder holder;

		public MyRunner(SessionViewHolder holder) {
			this.holder = holder;
		}

		@Override
		public void run() {
			
			while (isRunning) {
				if (mServiceBound) {
					activity.runOnUiThread(new Runnable() { // to avoid problem
															// with UI textview
															// update

						@Override
						public void run() {
							holder.chrono.setText(Utilities
									.getTime(mBoundService.getTimestamp()));
						}
					}); // runOnUithread
				}// if bound

				try {
					Thread.sleep(500); // update every half second
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * The holder for each ListView Session element
	 * 
	 * @author daniellando
	 *
	 */
	public static class SessionViewHolder {

		public TextView sessionName;
		public TextView falls;
		public TextView startTime;
		public TextView endTime;
		public TextView endText;
		public TextView durationTime;
		public TextView durationText;
		public RelativeLayout expandable;
		public ImageView playButton;
		public ImageView pauseButton;
		public ImageView moreButton;
		public ImageView fallIcon;
		public TextView chrono;


	}

}
