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
import android.widget.EditText;

public class LoginActivity extends Activity {
	SharedPreferences settings;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(getBaseContext(),StartUpActivity.class));
            	finish();
            	StartUpActivity.startup.finish();
            }
        });
        
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Button enter = (Button) findViewById(R.id.loginEnter);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	settings.edit().putString("netUsername", (String) username.getText().toString()).commit();
            	settings.edit().putString("netPassword", (String) password.getText().toString()).commit();
            	startActivity(new Intent(getBaseContext(),NetLobbyActivity.class));
            	finish();
            }
        });
    }
}