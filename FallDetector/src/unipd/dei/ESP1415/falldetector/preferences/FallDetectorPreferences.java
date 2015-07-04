package unipd.dei.ESP1415.falldetector.preferences;

import unipd.dei.ESP1415.falldetector.R;
import unipd.dei.ESP1415.falldetector.R.xml;
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