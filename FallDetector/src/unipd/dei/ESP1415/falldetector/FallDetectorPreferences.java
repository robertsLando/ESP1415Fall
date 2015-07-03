package unipd.dei.ESP1415.falldetector;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class FallDetectorPreferences extends PreferenceActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingFragment())
        .commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
	}
	
	
	
}