package unipd.dei.ESP1415.falldetector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


public class SettingFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	SharedPreferences.OnSharedPreferenceChangeListener mListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// upload of the preference layout
		addPreferencesFromResource(R.xml.preferences);
		
		
		mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences shared_prefs, String key) {
				// listener implementation
				if (key.equals("contact_list_add")) {
					Preference pref = findPreference("contact_list_management_2");
					if (pref instanceof CustomMultiSelectListPreference) {
						// Update display title
						// Write the description for the newly selected
						// preference
						// in the summary field.
						CustomMultiSelectListPreference listPref = (CustomMultiSelectListPreference) pref;
						listPref.updateEntriesAndValues(getActivity());
					}
				}
				else if(key.equals("pref_notification_activate")) {
					CheckBoxPreference mCheckBox = (CheckBoxPreference) findPreference("pref_notification_activate");
					if(!(mCheckBox.isChecked())){
						//if the button is unchecked -----> I have to do putBoolean("firstTime", true);
						SharedPreferences mTimeSettings = getActivity().getApplicationContext().getSharedPreferences("TimePickerSettings", Context.MODE_PRIVATE);
						SharedPreferences.Editor mEditor = mTimeSettings.edit();
						mEditor.putBoolean("firstTime", true);
						mEditor.commit();
					}
				}
			}
		};
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}
}
