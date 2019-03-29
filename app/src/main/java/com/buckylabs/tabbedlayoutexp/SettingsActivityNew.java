package com.buckylabs.tabbedlayoutexp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

public class SettingsActivityNew extends PreferenceActivity {
    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("example_switch", false)) {
            setTheme(R.style.PreferencesDarkTheme);
            // recreate();
        } else {
            setTheme(R.style.PreferencesTheme);
            // recreate();
        }
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();


        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // Implementation


                if (key.equals("example_switch")) {

                    boolean value = preferences.getBoolean("example_switch", false);

                    if (value) {

                        Intent i = new Intent(SettingsActivityNew.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    } else {

                        Intent i = new Intent(SettingsActivityNew.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();

                    }
                }


            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
            addPreferencesFromResource(R.xml.preferences);
            String path = Environment.getExternalStorageDirectory() + "/App_Backup_Pro/";
            Preference etp = findPreference("storage_path");
            etp.setSummary(path);
        }
    }
}