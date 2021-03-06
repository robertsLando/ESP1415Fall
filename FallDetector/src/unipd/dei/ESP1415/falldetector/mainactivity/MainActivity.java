package unipd.dei.ESP1415.falldetector.mainactivity;

import java.util.ArrayList;

import unipd.dei.ESP1415.falldetector.R;
import unipd.dei.ESP1415.falldetector.R.drawable;
import unipd.dei.ESP1415.falldetector.R.id;
import unipd.dei.ESP1415.falldetector.R.layout;
import unipd.dei.ESP1415.falldetector.R.menu;
import unipd.dei.ESP1415.falldetector.R.string;
import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.SessionDB.SessionTable;
import unipd.dei.ESP1415.falldetector.mainactivity.FloatingActionButton.Builder;
import unipd.dei.ESP1415.falldetector.preferences.FallDetectorPreferences;
import unipd.dei.ESP1415.falldetector.utilities.Utilities;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	ListView list; // the reference to the widget in main activity
	public static SessionViewAdapter adapter; // the adapter for listview manage
	public ArrayList<Session> listViewValues = new ArrayList<Session>(); // the
	public static Context mContext; // container
	private static FloatingActionButton fabButton;
	private static boolean sessionToComplete = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// draw the fab button
		fabButton = fabSetter();
		mContext = this.getBaseContext();

		// add the elements into the listview
		setListData();

		// get the listview widget
		list = (ListView) findViewById(R.id.sessionListView);

		// create a new adapter for the listview
		adapter = new SessionViewAdapter(this, listViewValues);
		list.setAdapter(adapter);

		// to hide action button when scrolling
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (!sessionToComplete) {
					switch (scrollState) {
					case OnScrollListener.SCROLL_STATE_IDLE:
						fabButton.showFloatingActionButton();
						break;
					case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
						fabButton.hideFloatingActionButton();
						break;
					case OnScrollListener.SCROLL_STATE_FLING:
						fabButton.hideFloatingActionButton();
						break;
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visibleItemCount == totalItemCount)
					return;

			}
		});

	}

	public void setListData() {

		/*
		 * for (int i = 0; i < 20; i++) {
		 * 
		 * final Session temp = new Session();
		 * 
		 * temp.setName("NomeSessioneeeeeeeeee"); temp.setEnd(0); // oggi
		 * temp.setStart(1430489157); // domani temp.setFalls(1000);
		 * 
		 * listViewValues.add(temp); }
		 */

		DbManager databaseManager = new DbManager(mContext);

		//databaseManager.updateDB(); //uncomment this line when update
		// database

		Cursor c = databaseManager.getSessions();
		
		while (c.moveToNext()) {

			final Session temp = new Session();

			temp.setId(c.getInt(SessionTable.ID));
			temp.setName(c.getString(SessionTable.NAME));
			temp.setEnd(c.getLong(SessionTable.END));
			temp.setStart(c.getLong(SessionTable.START));
			temp.setBgColor(c.getInt(SessionTable.BGCOLOR));
			temp.setImgColor(c.getInt(SessionTable.IMGCOLOR));
			temp.setFalls(c.getInt(SessionTable.FALLS));
			temp.setTimeElapsed(c.getInt(SessionTable.TIMEELAPSED));
			temp.setRunning((c.getInt(SessionTable.ISRUNNING) == 1) ? true
					: false);

			if (temp.getEnd() == 0) // there is a session to complete so I can't
									// start another one
			{
				fabButton.hideFloatingActionButton();
				sessionToComplete = true;
				listViewValues.add(0, temp); // it must be the first one
			}

			else
				listViewValues.add(temp);
		}

	}

	public static void completeSession() {
		fabButton.showFloatingActionButton();
		sessionToComplete = false;

	}

	@SuppressWarnings("deprecation")
	private FloatingActionButton fabSetter() {
		// Set ad final otherwise there are problem on the Listener
		final FloatingActionButton fabButton;

		fabButton = new FloatingActionButton.Builder(this)
				.withDrawable(
						getResources().getDrawable(
								R.drawable.ic_plus_black_24dp))
				.withButtonColor(Color.WHITE)
				.withGravity(Gravity.BOTTOM | Gravity.END)
				.withMargins(0, 0, 16, 16).create();

		fabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// custom dialog
				final Dialog dialog = new Dialog(v.getContext());
				dialog.setContentView(R.layout.new_session_dialog);
				dialog.setTitle(getString(R.string.newSession));

				// set the custom dialog components - text, image and button
				final EditText text = (EditText) dialog
						.findViewById(R.id.newSessionName);
				final ImageView image = (ImageView) dialog
						.findViewById(R.id.newSessionImage);

				final int[] color;

				color = Utilities.setRandomBg(image);

				Button dialogOkButton = (Button) dialog
						.findViewById(R.id.dialogButtonOK);
				Button dialogCancelButton = (Button) dialog
						.findViewById(R.id.dialogButtonCancel);
				// if button is clicked, close the custom dialog

				dialogOkButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Session temp = new Session();
						String name = text.getText().toString();
						if (!name.equals("")) {
							temp.setName(name);
							temp.setBgColor(color[0]);
							temp.setImgColor(color[1]);
							temp.setStart(0);

							DbManager databaseManager = new DbManager(mContext);

							long id = databaseManager.addSession(temp);

							if (id >= 0) {
								temp.setId(id);
								listViewValues.add(0, temp);
							}

							else
								Toast.makeText(v.getContext(),
										getString(R.string.error),
										Toast.LENGTH_SHORT).show();

							dialog.dismiss();
							fabButton.hideFloatingActionButton();
							sessionToComplete = true;

						} else
							Toast.makeText(v.getContext(),
									getString(R.string.errorEmptyName),
									Toast.LENGTH_SHORT).show();

					}
				});
				dialogCancelButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();

					}
				});
				dialog.show();

			}// onCLick FabButton

		});// setOnClickListener FabButon

		return fabButton;
	}// fabSetter

	public static FloatingActionButton getFAB() {
		return fabButton;
	}// getFAB

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this, FallDetectorPreferences.class);
			startActivity(intent);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}    

}
