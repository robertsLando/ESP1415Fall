package unipd.dei.ESP1415.falldetector;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// carico le preference che avevo fatto nella file xml nella cartella res/xml
		addPreferencesFromResource(R.xml.preferences);
	}

}
