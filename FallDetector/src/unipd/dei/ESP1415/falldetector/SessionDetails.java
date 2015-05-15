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
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SessionDetails extends ActionBarActivity implements OnItemClickListener{
	
	private Activity activity; // the activity where the ListView is placed
	public static Context sdContext;
	ListView list;
	public ArrayList<Fall> fallList = new ArrayList<Fall>();
	public FallViewAdapter adapter = null;
	
	public static final String FALL = "fall";
	
	private Session currentSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_details);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setHomeAsUpIndicator(
		//		R.drawable.ic_action_remove_white);
				
		sdContext = this.getBaseContext();	
		Intent myIntent = getIntent();
		currentSession = (Session) myIntent.getSerializableExtra(SessionViewAdapter.SESSION);
		
		
		/*
		 * set of instructions regarding the initialization of the list view
		 * which contains all the falls in this session
		 */
		setListData(currentSession.getId());
		
		this.adapter = new FallViewAdapter(this,
				R.layout.fall_list_item_layout, this.fallList);
		
		list = (ListView) findViewById(R.id.sessionListView);
		list.setAdapter(adapter);
		
		final TextView currentSessionName = (TextView) findViewById(R.id.CourrentSessionName);
		TextView sessionStartDate = (TextView) findViewById(R.id.startDateTextView);
		ImageView image = (ImageView) findViewById(R.id.courrentSessionImage);
		final ImageView btnEdit = (ImageView) findViewById(R.id.editButton);
		final ImageView btnDone = (ImageView) findViewById(R.id.doneButton);
		final EditText newSessionName = (EditText) findViewById(R.id.newSessionName);
		
		
		btnEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					btnEdit.setVisibility(View.GONE);
					btnDone.setVisibility(View.VISIBLE);
					newSessionName.setEnabled(true);
			}
		});
		
		btnDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				btnDone.setVisibility(View.GONE);
				btnEdit.setVisibility(View.VISIBLE);
				newSessionName.setEnabled(false);
				String name = newSessionName.getText().toString();
				if(!name.equals(""))
				{
					currentSession.setName(name);
					DbManager databaseManager = new DbManager(v.getContext());
					databaseManager.updateSession(currentSession);
					currentSessionName.setText(currentSession.getName());
				}
				else
					Toast.makeText(v.getContext(), activity.getString(R.string.errorEmptyName), Toast.LENGTH_SHORT).show();

				
			}
		});
		
		Utilities.setThumbnail(image, currentSession.getBgColor(), currentSession.getImgColor());

		Date sessionDate = new Date(currentSession.getStart());
		
		sessionStartDate.setText(Utilities.getDate(sessionDate));
		currentSessionName.setText(currentSession.getName());
		newSessionName.setText(currentSession.getName());

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		/*Object clickedObj = parent.getItemAtPosition(position);
		Log.i("[onItemClick]", clickedObj.toString());*/
		
		final Fall fl = (Fall) fallList.get(position);
		
		Intent myIntent = new Intent(v.getContext(), FallEvent.class);

		myIntent.putExtra(FALL, fl);
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
		switch (item.getItemId()) {
	    case android.R.id.home:
	    	Intent intent = new Intent(this.getBaseContext(),MainActivity.class);
		    startActivity(intent);

	        break;

	    default:
	        break;
	    }
		return super.onOptionsItemSelected(item);
	}
	
	
	

	public void setListData(long sID) {
		
		DbManager databaseManager = new DbManager(sdContext);

		//ok
		//databaseManager.updateDB(); //uncomment this line when update database
		//databaseManager.addTempFalls(sID);

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
