package unipd.dei.ESP1415.falldetector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.test.MoreAsserts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class sessionViewAdapter extends BaseAdapter implements OnClickListener {

	private Activity activity; // the activity where the ListView is placed
	private ArrayList<Session> sessionList; // the container
	private static LayoutInflater inflater = null; // calls external xml layout
													// ()
	private SessionViewHolder itemVisible = null;

	Session ses = null;
	int i = 0;

	public sessionViewAdapter(Activity mactivity, ArrayList<Session> data) {

		activity = mactivity;
		sessionList = data;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return sessionList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getView(int position, View mySessionView, ViewGroup parent) {

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

			mySessionView.setTag(holder);

		} else
			// the view already exist
			holder = (SessionViewHolder) mySessionView.getTag();

		ses = null;
		ses = (Session) sessionList.get(position);

		Date start = new Date(ses.getStart());
		Date end = new Date(ses.getEnd());

		long millis = ses.getEnd() - ses.getStart();

		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;

		String duration = String.format("%02d:%02d:%02d", hour, minute, second);

		// Set the value of the widgets
		holder.sessionName.setText(ses.getName());
		holder.falls.setText(String.valueOf(ses.getFalls()));
		holder.startTime.setText(getDate(start));
		holder.endTime.setText(getDate(end));
		holder.durationTime.setText(duration);
	

		// generate the random bgcolor not too dark
		Random rnd = new Random();
		int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256));
		
		while(isColorDark(color))
			color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
			
		
		holder.fallIcon.setBackgroundColor(color);
		

		if (ses.getEnd() == 0) // E' in esecuzione
		{
			holder.playButton.setVisibility(View.VISIBLE);
			holder.durationTime.setVisibility(View.GONE);
			holder.durationText.setVisibility(View.GONE);
			holder.endTime.setText("In execution");
		}

		holder.moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// Creating the instance of PopupMenu
				PopupMenu popup = new PopupMenu(v.getContext(), holder.moreButton);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.more_menu,
						popup.getMenu());

				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(v.getContext(),
								"You Clicked : " + item.getTitle(),
								Toast.LENGTH_SHORT).show();
						return true;
					}
				});

				popup.show(); // showing popup menu
			}
		}); // closing the setOnClickListener method

		holder.playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.pauseButton.setVisibility(View.VISIBLE);
				holder.playButton.setVisibility(View.GONE);

			}
		});

		holder.pauseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.playButton.setVisibility(View.VISIBLE);
				holder.pauseButton.setVisibility(View.GONE);

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

	/**
	 * A method to get a date in the format dd-MM-yyyy HH:mm:ss
	 * 
	 * @param date
	 *            the date to format
	 * @return the date formatted
	 */
	private String getDate(Date date) {

		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"dd-MM-yyyy HH:mm:ss");
			return format.format(date);
		} catch (Exception e) {
			Log.e("Date format EXCEPTION",
					"Something went wrong with date formatting");
			return null;
		}
	}
	
	private boolean isColorDark(int color){
	    double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
	    if(darkness<0.5){
	        return false; // It's a light color
	    }else{
	        return true; // It's a dark color
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
		public TextView durationTime;
		public TextView durationText;
		public RelativeLayout expandable;
		public ImageView playButton;
		public ImageView pauseButton;
		public ImageView moreButton;
		public ImageView fallIcon;

	}

}
