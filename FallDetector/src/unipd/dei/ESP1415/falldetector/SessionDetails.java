package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Date;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
import unipd.dei.ESP1415.falldetector.database.SessionDB.SessionTable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class SessionDetails extends Activity implements OnItemClickListener{
	
	private Activity activity; // the activity where the ListView is placed
	public static Context sdContext;
	ListView list;
	public ArrayList<Fall> fallList = new ArrayList<Fall>();
	public FallViewAdapter adapter = null;
	
	public static final String ID = "id";
	public static final String LOCATION = "location";
	public static final String DATE = "datef";
	public static final String SESSIONID = "sessionID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_details);
				
		sdContext = this.getBaseContext();
		
		Intent myIntent = getIntent();
		
		long sId = myIntent.getLongExtra(SessionViewAdapter.ID, -1);
		
		/**
		 * set of instructions regarding the initialization of the list view
		 * which contains all the falls in this session
		 */
		setListData(sId);
		
		this.adapter = new FallViewAdapter(this,
				R.layout.fall_list_item_layout, this.fallList);
		
		list = (ListView) findViewById(R.id.sessionListView);
		list.setAdapter(adapter);
		
		TextView currentSessionName = (TextView) findViewById(R.id.CourrentSessionName);
		TextView sessionStartDate = (TextView) findViewById(R.id.startDateTextView);
		
		long timestamp = myIntent.getLongExtra(SessionViewAdapter.START, 0);

		Date sessionDate = new Date(timestamp);
		
		sessionStartDate.setText(SessionViewAdapter.getDate(sessionDate));
		currentSessionName.setText(myIntent.getStringExtra(SessionViewAdapter.NAME));



	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		/**Object clickedObj = parent.getItemAtPosition(position);
		Log.i("[onItemClick]", clickedObj.toString());*/
		
		final Fall fl = (Fall) fallList.get(position);
		
		Intent myIntent = new Intent(v.getContext(), FallEvent.class);

		myIntent.putExtra(ID, fl.getId());
		myIntent.putExtra(LOCATION, fl.getLocation());
		myIntent.putExtra(DATE, fl.getDatef());
		myIntent.putExtra(SESSIONID, fl.getSessionID());

		activity.startActivity(myIntent);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.session_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	public void setListData(long sID) {
		
		DbManager databaseManager = new DbManager(sdContext);

		//ok
		//databaseManager.updateDB(); //uncomment this line when update database
		databaseManager.addTempFalls(sID);

		Cursor c = databaseManager.getFalls(sID);
		
		while(c.moveToNext()){
			
			final Fall temp = new Fall();

			temp.setId(c.getInt(FallTable.ID));
			temp.setLocation(c.getString(FallTable.LOCATION));
			temp.setDatef(c.getLong(FallTable.DATE)); 
			//temp.setSessionID(c.getInt(FallTable.SESSIONID));
			
			fallList.add(temp);		
		}

	}
}
