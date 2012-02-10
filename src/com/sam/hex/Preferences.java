package com.sam.hex;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	boolean CheckboxPreference;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);
        SharedPreferences prefs = this.getSharedPreferences("Settings",Activity.MODE_PRIVATE);
        CheckboxPreference = prefs.getBoolean("checkboxPref", true);
    }
}