package com.sam.hex.net;

import com.sam.hex.R;
import com.sam.hex.startup.StartUpActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class NetLobbyActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("netUsername", "").equals("") || prefs.getString("netPassword", "").equals("")){
        	startActivity(new Intent(getBaseContext(),LoginActivity.class));
        	finish();
        }
        
        setContentView(R.layout.netlobby);
        
        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(),StartUpActivity.class));
            	finish();
            	StartUpActivity.startup.finish();
            }
        });
    }
}