package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;
import java.util.Date;

import unipd.dei.ESP1415.falldetector.SessionViewAdapter.SessionViewHolder;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The customized adapter for the falls list view
 * 
 * @author thomasgagliardi
 *
 */
public class FallViewAdapter extends ArrayAdapter<Fall> {
	
	private ArrayList<Fall> items;
	private Context context;
	private static LayoutInflater inflater = null;
	
	public FallViewAdapter(Context context, int textViewResourceId,
			ArrayList<Fall> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// performance issue (see Google I/O video in references)
		
		final SesDetailsViewHolder holder;
		
		View v = convertView;
		
		if (v == null) {
			
			v = inflater.inflate(R.layout.fall_list_item_layout, null);
			// create the item holder
			holder = new SesDetailsViewHolder();
			// Get widgets from the view
			holder.fallIcon = (ImageView) v
					.findViewById(R.id.fallDtls_ic);
			holder.fallId = (TextView) v
					.findViewById(R.id.fallIdTextView);
			holder.startLabel = (TextView) v
					.findViewById(R.id.fallTimeLabel);
			holder.startTime = (TextView) v
					.findViewById(R.id.fallTimeTextView);

			v.setTag(holder);
		} else
			// the view already exist
			holder = (SesDetailsViewHolder) v.getTag();
		
		
		// position contains the index of the array for
		// the associated item so we retrieve the Contact
		Fall c = this.items.get(position);
		if (c != null) {
			holder.fallId.setText(": "+c.getId());
			
			Date sessionDate = new Date(c.getDatef());
			
			holder.startTime.setText(Utilities.getDate(sessionDate));
		}
		
		v.setOnClickListener(new OnItemClickListener(position));
		
		return v;
	}
	
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View myView) {

			SessionDetails sct = (SessionDetails) context;
			sct.onItemClick(mPosition, myView);
		}
	}
	
	/**
	 * The holder for each ListView fall element
	 * 
	 * @author thomasgagliardi
	 *
	 */
	
	public static class SesDetailsViewHolder {

		public ImageView fallIcon;
		public TextView fallId;
		public TextView startLabel;
		public TextView startTime;
	}
	
}
