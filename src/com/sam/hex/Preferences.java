package com.sam.hex;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	boolean CheckboxPreference;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
        
        OnPreferenceChangeListener nameListener = new OnPreferenceChangeListener() {        
            @Override
            public boolean onPreferenceChange(Preference pref, Object newValue) {
                pref.setSummary("Name: "+newValue.toString());
                return false;
            }
        };
        
        Preference p1Pref = findPreference("player1Name");
        p1Pref.setOnPreferenceChangeListener(nameListener);
        Preference p2Pref = findPreference("player2Name");
        p2Pref.setOnPreferenceChangeListener(nameListener);
    }
}