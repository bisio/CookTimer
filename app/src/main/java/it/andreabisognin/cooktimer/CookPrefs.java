package it.andreabisognin.cooktimer;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by bisio on 1/9/15.
 */
public class CookPrefs extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference preference = findPreference(getString(R.string.pref_timestep_key));
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(),""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        preference.setSummary((String) value);
        return true;
    }
}
