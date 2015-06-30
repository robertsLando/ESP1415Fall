package unipd.dei.ESP1415.falldetector;

import java.util.Set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class FallDetectorPreferences extends PreferenceActivity {
	
	//I need this string to use SharedPreference in all activity of my application
		public static final String SharedPrefName = "Settings";
		
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
	
	protected void onStop(){
		
	    //on stop, when the settings are no longer visible we save all the value of the settings in SharedPreferences
		//we call our preference by name
		SharedPreferences prefs = getSharedPreferences(SharedPrefName, MODE_PRIVATE);
		
		//we need an Editor to edit the preference
		SharedPreferences.Editor mPrefsEditor = prefs.edit();
		//now we have to insert all the couple (key/value) of our preference
		//for the first time we use default value
		sampleRate = (ListPreference) findPreference("accelerometer_settings");
		String rateValue = sampleRate.getValue();
		mPrefsEditor.putString("accelerometer_settings", rateValue);
		
		sessionDuration = (ListPreference) findPreference("session_duration");
		String sesDuration = sessionDuration.getValue();
		mPrefsEditor.putString("session_duration", sesDuration);
		
		helper = (CustomMultiSelectListPreference) findPreference("contact_list_management_2");
		Set<String> helperChecked = helper.getValues();
		mPrefsEditor.putStringSet("contact_list_management_2", helperChecked);
		
		//we save all the value
		mPrefsEditor.commit();
		
		
		
	}
	
	
}