package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	ListView list; // the reference to the widget in main activity
	SessionViewAdapter adapter; // the adapter for listview manage
	public ArrayList<Session> listViewValues = new ArrayList<Session>(); // the
																			// container
	private static FloatingActionButton fabButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// draw the fab button
		fabButton = fabSetter();

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

		// TODO: Add database support fetching
		for (int i = 0; i < 8; i++) {

			final Session temp = new Session();

			temp.setName("NomeSessioneeeeeeeeee");
			temp.setEnd(0); // oggi
			temp.setStart(1430489157); // domani
			temp.setFalls(1000);

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
				if (!fabButton.isHidden()) {
					Toast.makeText(v.getContext(), "Hai premuto il FAB",
							Toast.LENGTH_SHORT).show();
				}// if
			}// onCLick
		});// setOnClickListener

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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
