package com.sam.hex;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class StartUpActivity extends Activity {
	private int gametype;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        

    	//Load preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	gametype = Integer.decode(prefs.getString("player2Type", "0"));
        
        //First button
        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(gametype == 2){
            		Intent gameActivity = new Intent(getBaseContext(),LocalLobbyActivity.class);
            		startActivity(gameActivity);
            	}
            	else{
            		Intent gameActivity = new Intent(getBaseContext(),HexGame.class);
            		startActivity(gameActivity);
            	}
            }
        });
        
        //Second button
        final Button instructionsButton = (Button) findViewById(R.id.instructionsButton);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent instructionsActivity = new Intent(getBaseContext(),InstructionsActivity.class);
            	startActivity(instructionsActivity);
            }
        });
        
        //Third button
        final Button optionsButton = (Button) findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent settingsActivity = new Intent(getBaseContext(),Preferences.class);
            	startActivity(settingsActivity);
            }
        });
    }
    
    @Override
    public void onResume(){
    	super.onResume();

    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	gametype = Integer.decode(prefs.getString("player2Type", "0"));
    	
    	//Refresh first button
        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(gametype == 2){
            		Intent gameActivity = new Intent(getBaseContext(),LocalLobbyActivity.class);
            		startActivity(gameActivity);
            	}
            	else{
            		Intent gameActivity = new Intent(getBaseContext(),HexGame.class);
            		startActivity(gameActivity);
            	}
            }
        });
        if(Global.moveList.size()==0 || Integer.decode(prefs.getString("aiPref", "1")) != Global.difficulty || Integer.decode(prefs.getString("gameSizePref", "7")) != Global.gridSize || Integer.decode(prefs.getString("player1Type", "0")) != (int) Global.player1Type || Integer.decode(prefs.getString("player2Type", "0")) != (int) Global.player2Type){
        	startButton.setText("Start Game");
    	}
        else{
        	startButton.setText("Resume Game");
        }
    }
}