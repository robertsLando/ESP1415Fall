package unipd.dei.ESP1415.falldetector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class FallDetectorPreferences extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingFragment())
        .commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		//now we have to save all the values in a SharedPreferences
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		
		

		
	}
	
	
}