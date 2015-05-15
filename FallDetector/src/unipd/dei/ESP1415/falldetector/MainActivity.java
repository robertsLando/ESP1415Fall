package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.SessionDB.SessionTable;
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
	public static Context mContext;																	// container
	private static FloatingActionButton fabButton;

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
		
		//to hide action button when scrolling
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			
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

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(visibleItemCount == totalItemCount)
					return;
					
				
			}
		});

	}

	public void setListData() {

		/*
		for (int i = 0; i < 20; i++) {

			final Session temp = new Session();

			temp.setName("NomeSessioneeeeeeeeee");
			temp.setEnd(0); // oggi
			temp.setStart(1430489157); // domani
			temp.setFalls(1000);

			listViewValues.add(temp);
		}*/
		
		DbManager databaseManager = new DbManager(mContext);
	
		
		//databaseManager.updateDB(); //uncomment this line when update database 
		
		Cursor c = databaseManager.getSessions();
		
		while(c.moveToNext()){
			
			final Session temp = new Session();

			temp.setId(c.getInt(SessionTable.ID));
			temp.setName(c.getString(SessionTable.NAME));
			temp.setEnd(c.getLong(SessionTable.END)); 
			temp.setStart(c.getLong(SessionTable.START)); 
			temp.setBgColor(c.getInt(SessionTable.BGCOLOR));
			temp.setImgColor(c.getInt(SessionTable.IMGCOLOR)); 
			temp.setFalls(c.getInt(SessionTable.FALLS));

			listViewValues.add(temp);
		}

	}

	public void onItemClick(int mPosition) {
		Session temp = (Session) listViewValues.get(mPosition);

		// Show the alert
		Toast.makeText(this, "Hai premuto l'elemento: " + temp.getName(),
				Toast.LENGTH_LONG).show();

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
					final EditText text = (EditText) dialog.findViewById(R.id.newSessionName);
					final ImageView image = (ImageView) dialog.findViewById(R.id.newSessionImage);
					
					final int[] color;
					
					color = Utilities.setRandomBg(image);
					
					Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
					Button dialogCancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
					// if button is clicked, close the custom dialog
					
					dialogOkButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							Session temp = new Session();
							String name = text.getText().toString();
							if(!name.equals(""))
							{
								temp.setName(name);
								temp.setBgColor(color[0]);
								temp.setImgColor(color[1]);
								temp.setStart(System.currentTimeMillis());
								
								DbManager databaseManager = new DbManager(mContext);
								
								long id = databaseManager.addSession(temp);
								
								if(id >= 0)
								{
									temp.setId(id);
									listViewValues.add(temp);
								}
								
								else 
									Toast.makeText(v.getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
								
								dialog.dismiss();
							}
							else
								Toast.makeText(v.getContext(), getString(R.string.errorEmptyName), Toast.LENGTH_SHORT).show();

								
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent();
            intent.setClassName("unipd.dei.ESP1415.falldetector", "unipd.dei.ESP1415.falldetector.FallDetectorPreferences");
            startActivity(intent);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
