package unipd.dei.ESP1415.falldetector;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import unipd.dei.ESP1415.falldetector.SettingFragment; 

public class FallDetectorPreferences extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingFragment())
        .commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}
	
	
}