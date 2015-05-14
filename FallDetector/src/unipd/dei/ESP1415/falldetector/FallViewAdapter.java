package unipd.dei.ESP1415.falldetector;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * The customized adapter for the falls list view
 * 
 * @author thomasgagliardi
 *
 */
public class FallViewAdapter extends ArrayAdapter<Fall>{
	
	private ArrayList<Fall> items;
	private Context context;
	
	public FallViewAdapter(Context context, int textViewResourceId,
			ArrayList<Fall> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// performance issue (see Google I/O video in references)
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fall_list_item_layout, null);
		}
		// position contains the index of the array for
		// the associated item so we retrieve the Contact
		Fall c = this.items.get(position);
		if (c != null) {
			TextView fallIDTextView = (TextView) v.findViewById(R.id.fallIdTextView);
			fallIDTextView.setText(": "+c.getId());
			TextView fallTimeTextView = (TextView) v.findViewById(R.id.fallTimeTextView);
			fallTimeTextView.setText(""+c.getDatef());
		}
		return v;
	}
}
