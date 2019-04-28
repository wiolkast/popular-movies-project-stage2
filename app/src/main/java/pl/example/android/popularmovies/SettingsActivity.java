package pl.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class MoviesPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.settings_main);

            PreferenceScreen preferenceScreen = getPreferenceScreen();
            for(int i=0; i<preferenceScreen.getPreferenceCount(); i++){
                Preference preference = preferenceScreen.getPreference(i);
                preference.setOnPreferenceChangeListener(this);

                if(preference instanceof ListPreference){
                    setListPreferenceSummary((ListPreference) preference,
                            ((ListPreference) preference).getValue());
                } else if (preference instanceof EditTextPreference) {
                    preference.setSummary(hideSummary(((EditTextPreference) preference).getText()));
                }
            }
        }

        private void setListPreferenceSummary(ListPreference listPreference, String value){
                int id = listPreference.findIndexOfValue(value);
                if(id >= 0){
                    listPreference.setSummary(listPreference.getEntries()[id]);
                }
            }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if(preference instanceof ListPreference){
                setListPreferenceSummary((ListPreference) preference, o.toString());
            } else {
                preference.setSummary(hideSummary(o.toString()));
            }
            return true;
        }

        private String hideSummary(String summary){
            if (summary != null) {
                return summary.replaceAll(".", "*");
            } else {
                return "";
            }
        }
    }
}
