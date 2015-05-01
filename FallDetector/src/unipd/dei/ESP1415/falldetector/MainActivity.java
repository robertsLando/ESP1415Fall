package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	ListView list; //the reference to the widget in main activity
	sessionViewAdapter adapter; //the adapter for listview manage
	public ArrayList<Session> listViewValues = new ArrayList<Session>(); //the container

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//add the elements into the listview
		setListData();
		
		//get the listview widget
		list = (ListView) findViewById(R.id.sessionListView); 
		
		//create a new adapter for the listview
		adapter = new sessionViewAdapter(this, listViewValues);
		list.setAdapter(adapter);

	}

	public void setListData() {

		//TODO: Add database support fetching
		for (int i = 0; i < 11; i++) {

			final Session temp = new Session();

			temp.setName("NomeSex");
			temp.setEnd(1430575557); //oggi
			temp.setStart(1430489157); //domani
			temp.setFalls(11);

			listViewValues.add(temp);
		}

	}

	public void onItemClick(int mPosition)
    {
        Session temp = (Session) listViewValues.get(mPosition);                  
        
        //Show the alert
        Toast.makeText(this,"Hai premuto l'elemento: " + temp.getName(),
                Toast.LENGTH_LONG)
        .show();
        
    }

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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
