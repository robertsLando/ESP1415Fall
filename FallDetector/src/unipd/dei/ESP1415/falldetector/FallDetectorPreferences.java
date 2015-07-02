package unipd.dei.ESP1415.falldetector;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class FallDetectorPreferences extends PreferenceActivity {
	
		//we create a button to our preferences
	    private ListPreference sampleRate;
	    private ListPreference sessionDuration;
	    private CustomMultiSelectListPreference helper;


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingFragment())
        .commit();
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
	}
	
	
	
}