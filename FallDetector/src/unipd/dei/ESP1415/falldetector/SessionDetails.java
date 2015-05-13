package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Date;

import unipd.dei.ESP1415.falldetector.database.DbManager;
import unipd.dei.ESP1415.falldetector.database.FallDB.FallTable;
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
	public ArrayList<String> listViewValues = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_details);
				
		sdContext = this.getBaseContext();
		
		Intent myIntent = getIntent();
		
		int sId = myIntent.getIntExtra(SessionViewAdapter.ID, -1);
		
		/**
		 * set of instructions regarding the initialization of the list view
		 * which contains all the falls in this session
		 */
		setListData(sId);
		ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listViewValues);
		
		list = (ListView) findViewById(R.id.sessionListView);
		list.setAdapter(arrAdapter);
		list.setOnItemClickListener(this);

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
		
		Intent myIntent = new Intent(v.getContext(), FallEvent.class);

		/**myIntent.putExtra(ID, ses.getId());
		myIntent.putExtra(NAME, ses.getName());
		myIntent.putExtra(START, ses.getStart());
		myIntent.putExtra(END, ses.getName());*/

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
	
	/**private String[] fillStringArrayWithData() {
		String[] a = new String[('Z' - 'A' + 1)];
		for (int i = 0; i < a.length; ++i) {
			char[] c = { (char) ('A' + i) };
			a[i] = new String(c);
		}
		return a;
	}*/

	public void setListData(int sID) {
		
		DbManager databaseManager = new DbManager(sdContext);

		//ok
		//databaseManager.updateDB(); //uncomment this line when update database
		databaseManager.addTempFalls(sID);

		Cursor c = databaseManager.getFalls(sID);

		int i = 0;
		String temp = ""+i+" ";
		
		while(c.moveToNext()){
			
			temp = c.getString(FallTable.DATE);
			i++;
			listViewValues.add(temp);			
		}

	}
}
