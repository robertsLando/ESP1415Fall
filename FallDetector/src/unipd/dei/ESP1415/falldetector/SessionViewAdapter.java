package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Date;

import unipd.dei.ESP1415.falldetector.FallService.MyBinder;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
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

	private FallService mBoundService; // the instance of the service
	public boolean mServiceBound = false; // bind of service true = service is
											// bounded (mainactivity) false =
											// not buonded
	private Thread chronoThread;
	private boolean isRunning = false;

	// Intent string constants
	public static final String SESSION = "session";
	public static final String ELAPSED = "elapsed";

	private MyServiceConnection mServiceConnection;

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

	@SuppressLint("InflateParams")
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

		// Set the value of the widgets
		holder.sessionName.setText(ses.getName());
		holder.falls.setText(String.valueOf(ses.getFalls()));

		Utilities.setThumbnail(holder.fallIcon, ses.getBgColor(),
				ses.getImgColor());

		if (ses.getEnd() == 0) { // not ended

			holder.pauseButton.setVisibility(View.GONE);
			holder.chrono.setVisibility(View.VISIBLE);
			holder.endTime.setVisibility(View.GONE);
			holder.endText.setVisibility(View.INVISIBLE);
			holder.playButton.setVisibility(View.VISIBLE);
			holder.durationTime.setVisibility(View.GONE);
			holder.durationText.setVisibility(View.GONE);
			holder.expandable.setVisibility(View.VISIBLE);
			itemVisible = holder;
			mySessionView.setBackgroundColor(Color.GREEN);

			if (ses.isRunning())
				startChronometer(ses, holder, mySessionView.getContext(),
						position);

			else
				holder.chrono.setText(Utilities.getTime(ses.getTimeElapsed()));

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

							// remove the session from the database
							databaseManager.removeSession(ses.getId());

							// remove the session from the listview
							sessionList.remove(position);
							adapter.notifyDataSetChanged();

							if (ses.getEnd() == 0) {// the user have deleted the
													// session to complete

								MainActivity.completeSession(); // show the fab
																// button

								itemRunning = null;
								
								if(isRunning)
								{
									chronoThread.interrupt();
									chronoThread = null;
								}
								
								isRunning = false;
								

								// UNBIND and STOP service
								if (mServiceBound)
									activity.unbindService(mServiceConnection);
								
								if(FallService.isCreated)
								{
									Intent intent = new Intent(v.getContext(),
											FallService.class);
									activity.stopService(intent);
								}
							}

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

							long timeElapsed = 0;
							itemRunning = null;

							if (isRunning) // if chrono is running I stop it and
											// I get the time elapsed from the
											// service
							{
								chronoThread.interrupt();
								mBoundService.pause();
								timeElapsed = mBoundService.getTimestamp();
							} else 
								timeElapsed = ses.getTimeElapsed();
						
							
							//unbind the service
							if(mServiceBound)
							{
								activity.unbindService(mServiceConnection);
								mServiceBound = false;
							}
							
						
							chronoThread = null;
							isRunning = false;

							// stop the service
							Intent intent = new Intent(v.getContext(),
									FallService.class);
							boolean test = activity.stopService(intent);

							databaseManager = new DbManager(v.getContext());
							
							test = true;
							
							// update the session info in the database
							databaseManager.updateTimeElapsed(ses.getId(),
									timeElapsed);
							databaseManager.updateStatus(ses.getId(), false);
							databaseManager.updateEnd(ses.getId());

							// update the session list element info
							sessionList.get(position).setTimeElapsed(
									timeElapsed);
							sessionList.get(position).setEnd(
									System.currentTimeMillis());
							sessionList.get(position).setRunning(false);

							displayDuration(holder, ses);
							MainActivity.completeSession(); // show the fab
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
				startChronometer(ses, holder, v.getContext(), position);

			}// onClick playButton
		});// OnClickListener playButton

		holder.pauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.playButton.setVisibility(View.VISIBLE);
				holder.pauseButton.setVisibility(View.GONE);

				itemRunning = null;
				isRunning = false;
				chronoThread.interrupt();
				chronoThread = null;

				long timeelapsed = mBoundService.getTimestamp();
				mBoundService.pause();

				// update the session in the sessionList
				sessionList.get(position).setTimeElapsed(timeelapsed);
				sessionList.get(position).setRunning(false);

				// update the session in the db
				DbManager databaseManager = new DbManager(v.getContext());
				databaseManager.updateTimeElapsed(ses.getId(), timeelapsed);
				databaseManager.updateStatus(ses.getId(), false);

			}

		});

		mySessionView.setOnClickListener(new OnItemClickListener(position));

		return mySessionView;

	}

	/**
	 * This class implements OnClickListener class to handle the click event on
	 * ListView Items
	 * 
	 * @author daniellando
	 */
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

		}
	}

	/**
	 * This method is used to start details activity
	 * 
	 * @param c
	 *            The context
	 * @param ses
	 *            The Session to display
	 */
	private void displayDetails(Context c, Session ses) {
		Intent myIntent = new Intent(c, SessionDetails.class);
		myIntent.putExtra(SESSION, ses);
		activity.startActivity(myIntent);
	}

	/**
	 * This method display duration TextViews and hide the play/pause button and
	 * chrono
	 * 
	 * @param holder
	 *            The view holder
	 * @param ses
	 *            The Session object associated to the holder
	 */
	private void displayDuration(SessionViewHolder holder, Session ses) {
		holder.chrono.setVisibility(View.GONE);
		holder.endTime.setVisibility(View.VISIBLE);
		holder.endText.setVisibility(View.VISIBLE);
		holder.playButton.setVisibility(View.GONE);
		holder.pauseButton.setVisibility(View.GONE);
		holder.durationTime.setVisibility(View.VISIBLE);
		holder.durationText.setVisibility(View.VISIBLE);
		holder.endTime.setText(Utilities.getDate(new Date(ses.getEnd())));

		String duration = Utilities.getTime(ses.getTimeElapsed());
		holder.durationTime.setText(duration);
	}

	/**
	 * Start the chronometer of FallService class
	 * 
	 * @param ses
	 *            The session associated
	 * @param holder
	 *            The session view holder
	 * @param c
	 *            The context
	 * @param position
	 *            The position of the session in the listview
	 */
	public void startChronometer(Session ses, SessionViewHolder holder,
			Context c, int position) {
		
		holder.pauseButton.setVisibility(View.VISIBLE);
		holder.playButton.setVisibility(View.GONE);

		// the service hasn't been already bounded
		if (!mServiceBound) {

			// First I create an object MyServiceConnection that implements
			// ServiceConnection
			// and I bind the service, the service doesn't bind immediatly so I
			// don't start the chronometer here
			// but I recall this method (startChronometer) in the overrided
			// method onServiceConnected
			// of MyServiceConnection, this is the only way to ensure that the
			// service is bounded when
			// I call service methods
			mServiceConnection = new MyServiceConnection(ses, holder, c,
					position);

			Intent intent = new Intent(activity.getApplicationContext(),
					FallService.class);

			if (!FallService.isCreated()) {
				// start the service
				activity.startService(intent);
				intent.putExtra(ELAPSED, ses.getTimeElapsed());
			}

			// bind the service
			activity.bindService(intent, mServiceConnection,
					Context.BIND_AUTO_CREATE);
		} else {

			if (ses.getStart() == 0) {

				// save the start time in the db
				DbManager dbmanager = new DbManager(c);
				dbmanager.updateStart(ses.getId());
				// update the list item and the view
				ses.setStart(System.currentTimeMillis());
				sessionList.get(position).setStart(ses.getStart());
				holder.startTime.setText(Utilities.getDate(new Date(System
						.currentTimeMillis())));

			}

			// I call resume instead of start. This because the chrono is
			// started in
			// onBind method of the service so if the chrono is already started
			// this call
			// doesn't do anything but if it has been resumed it calculate the
			// pause time
			// and than restart the chrono
			mBoundService.resume();

			isRunning = true;
			itemRunning = holder;

			// update session status in sessionlist and database
			sessionList.get(position).setRunning(true);
			DbManager databaseManager = new DbManager(c);
			databaseManager.updateStatus(ses.getId(), true);

			// start the thread that updates the value of the elapsed time
			// every second
			chronoThread = new Thread(new MyRunner(holder, ses));

			chronoThread.start();
		}

	}

	/**
	 * This class monitored the state of the service connection
	 * 
	 * @author daniellando
	 *
	 */
	private class MyServiceConnection implements ServiceConnection {

		private Session ses;
		private SessionViewHolder holder;
		private int position;
		private Context context;

		public MyServiceConnection(Session ses, SessionViewHolder holder,
				Context c, int position) {
			this.ses = ses;
			this.holder = holder;
			this.position = position;
			this.context = c;
		} // an

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

			startChronometer(ses, holder, context, position);
		}
	}

	/**
	 * This class implements the Runnable class to manage the Chronometer of the
	 * FallService with a thread that updates chrono TextView
	 * 
	 * @author daniellando
	 *
	 */
	private class MyRunner implements Runnable {

		SessionViewHolder holder;

		long time;

		public MyRunner(SessionViewHolder holder, Session ses) {
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
							time = mBoundService.getTimestamp();
							holder.chrono.setText(Utilities.getTime(time));
						}
					}); // runOnUithread
				}// if bound

				try {

					Thread.sleep(1000); // update every second
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
	private class SessionViewHolder {

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
