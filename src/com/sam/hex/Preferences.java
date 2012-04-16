package com.sam.hex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.EditText;

public class Preferences extends PreferenceActivity {
	SharedPreferences settings;
	Preference p1Pref;
	Preference p2Pref;
	Preference lanPlPref;
	Preference resetPref;
	EditTextPreference customGridPref;
	PreferenceScreen screen;
	Preference gridPref;
	Preference replayPref;
	Preference loadReplayPref;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        
        loadPreferences();
    }
    
    class locListener implements OnPreferenceChangeListener{        
        @Override
        public boolean onPreferenceChange(Preference pref, Object newValue) {
        	settings.edit().putString(pref.getKey(), (String) newValue).commit();
        	screen.removeAll();
        	loadPreferences();
            
            setListeners();
            return true;
        }
    }
    
    class nameListener implements OnPreferenceChangeListener{        
        @Override
        public boolean onPreferenceChange(Preference pref, Object newValue) {
            pref.setSummary(getApplicationContext().getString(R.string.player2NameSummary_onChange)+" "+newValue.toString());
            return true;
        }
    }
    
    class resetListener implements OnPreferenceClickListener{        
        @Override
        public boolean onPreferenceClick(Preference pref) {
        	//Clear everything
    		PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().commit();
    		//Reload settings
        	startActivity(new Intent(getBaseContext(),Preferences.class));
        	finish();
            return false;
        }
    }
    
    class gridListener implements OnPreferenceChangeListener{
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(newValue.toString().equals("0")){
				//Custom value needed
				showInputDialog(getApplicationContext().getString(R.string.customGameSizeSummary));
			}
			else{
				preference.setSummary(getApplicationContext().getString(R.string.gameSizeSummary_onChange)+newValue.toString()+"x"+newValue.toString()+")");
			}
			return true;
		}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	setListeners();
    }
    
    private void setListeners(){
    	//Hide player2 unless the game location is on a single phone
    	Preference gameLoc = findPreference("gameLocation"); 
    	gameLoc.setOnPreferenceChangeListener(new locListener());
    	
    	//Change the summary to show the player's name
        p1Pref = findPreference("player1Name");
        if(p1Pref!=null){
        	p1Pref.setSummary(getApplicationContext().getString(R.string.player1NameSummary_onChange)+" "+settings.getString("player1Name", "Player1"));
        	p1Pref.setOnPreferenceChangeListener(new nameListener());
        }
        p2Pref = findPreference("player2Name");
        if(p2Pref!=null){
	        p2Pref.setSummary(getApplicationContext().getString(R.string.player2NameSummary_onChange)+" "+settings.getString("player2Name", "Player2"));
	        p2Pref.setOnPreferenceChangeListener(new nameListener());
        }
        lanPlPref = findPreference("lanPlayerName");
        if(lanPlPref!=null){
	        lanPlPref.setSummary(getApplicationContext().getString(R.string.lanPlayerNameSummary_onChange)+" "+settings.getString("lanPlayerName", "Player"));
	        lanPlPref.setOnPreferenceChangeListener(new nameListener());
        }
        
        //Set up the code to return everything to default
        resetPref = findPreference("resetPref");
        resetPref.setOnPreferenceClickListener(new resetListener());
        
        //Allow for custom grid sizes
        gridPref = findPreference("gameSizePref");
        if(settings.getString("gameSizePref", "7").equals("0")) gridPref.setSummary(InsertName.insert(getApplicationContext().getString(R.string.gameSizeSummary_onChange), settings.getString("customGameSizePref", "7")));
        else gridPref.setSummary(InsertName.insert(getApplicationContext().getString(R.string.gameSizeSummary_onChange), settings.getString("gameSizePref", "7")));
        gridPref.setOnPreferenceChangeListener(new gridListener());
    }
    
    private void loadPreferences(){
    	addPreferencesFromResource(R.layout.preferences_general);
        ListPreference val = (ListPreference) findPreference("gameLocation");
        if(val.getValue().equals("0")){
        	addPreferencesFromResource(R.layout.preferences_player1);
    		addPreferencesFromResource(R.layout.preferences_player2);
    	}
        else if(val.getValue().equals("1")){
        	addPreferencesFromResource(R.layout.preferences_lanplayer);
        }
        else if(val.getValue().equals("2")){
        	addPreferencesFromResource(R.layout.preferences_netplayer);
        }
    	addPreferencesFromResource(R.layout.preferences_reset);
    	
    	//Hide custom grid size preference
        customGridPref = (EditTextPreference) findPreference("customGameSizePref");
        screen = (PreferenceScreen) findPreference("preferences");
        screen.removePreference(customGridPref);
    }
    
    /**
     * Popup for custom grid sizes
     * */
    private void showInputDialog(String message){
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder     
        .setTitle(message)
        .setView(editText)
        .setPositiveButton("OK", new OnClickListener(){
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			int input = Integer.decode(editText.getText().toString());
    			if(input>30){
    				input = 30;
    			}
    			else if(input<4){
    				input = 4;
    			}
    			settings.edit().putString("customGameSizePref", input+"").commit();
    			gridPref.setSummary(InsertName.insert(getApplicationContext().getString(R.string.gameSizeSummary_onChange), settings.getString("customGameSizePref", "7")));
    		}
        })
        .setNegativeButton("Cancel", null)
        .show();
    } 
}