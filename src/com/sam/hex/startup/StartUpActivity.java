package com.sam.hex.startup;

import com.sam.hex.Global;
import com.sam.hex.HexGame;
import com.sam.hex.Preferences;
import com.sam.hex.R;
import com.sam.hex.lan.LocalLobbyActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class StartUpActivity extends Activity {
	private Button[] mButton = new Button[3];
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        FrameLayout page = new FrameLayout(this);
//        StartUpView background = new StartUpView (this);
//        FrameLayout buttons = new FrameLayout (this);
//        
//        for(int i=0;i<3;i++){
//        	mButton[i] = new Button(this);
//			mButton[i].setWidth(100);
//			mButton[i].setHeight(100);
//			if(i==0){
//				mButton[i].setId(R.id.startButton);
//				mButton[i].setText(R.string.start);
//			}
//			if(i==1){
//				mButton[i].setId(R.id.instructionsButton);
//				mButton[i].setText(R.string.rules);
//			}
//			if(i==2){
//				mButton[i].setId(R.id.optionsButton);
//				mButton[i].setText(R.string.options);
//			}
//			
//			FrameLayout.LayoutParams absParams = new FrameLayout.LayoutParams(100,100);
//			absParams.leftMargin = 100*i;//x cord
//			absParams.topMargin = 100*i;//y cord
//			mButton[i].setLayoutParams(absParams);
//			
//			buttons.addView(mButton[i]);
//        }       
//
//        page.addView(background);
//        page.addView(buttons);
//
//        setContentView(page);
        
        
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

    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	//Refresh first button
        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(Integer.parseInt(prefs.getString("gameLocation", "0")) == 1 && (HexGame.somethingChanged(prefs) || HexGame.startNewGame)){
                	startActivity(new Intent(getBaseContext(),LocalLobbyActivity.class));
            	}
            	else{
            		startActivity(new Intent(getBaseContext(),HexGame.class));
            	}
            }
        });
        if(HexGame.somethingChanged(prefs) || HexGame.startNewGame){
        	HexGame.startNewGame = true;
        	startButton.setText(R.string.start);
    	}
        else{ 
        	startButton.setText(R.string.resume);
        }
        
        //Set player names
    	Global.player1Name = prefs.getString("player1Name", "Player1");
    	Global.player2Name = prefs.getString("player2Name", "Player2");
    }
}