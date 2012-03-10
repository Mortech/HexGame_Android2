package com.sam.hex;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LocalLobbyActivity extends Activity {
	private List<String> values = new ArrayList<String>();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locallobby);
        
        Intent intent = new Intent();
        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,-1);

        switch(state){
        case WifiManager.WIFI_STATE_DISABLED:
        	Toast.makeText(this, "Wifi off", Toast.LENGTH_LONG).show();
            break;

        case WifiManager.WIFI_STATE_ENABLED:
        	Toast.makeText(this, "Wifi on", Toast.LENGTH_LONG).show();
            break;
        }

        
        //TODO Create a thread that loops, looking for players to add
        
        //TODO Create a listener that runs challengeRecieved() if someone calls us
    }
    
    private void newPlayerFound(String playerName){
    	values.add(playerName);
    	
    	ListView playerList = (ListView) findViewById(R.id.players);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, values);
        playerList.setAdapter(adapter);
    }
    
    private void challengeRecieved(String ipAddress){
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        case DialogInterface.BUTTON_POSITIVE:
    	            //Yes button clicked
    	        	startActivity(new Intent(getBaseContext(),HexGame.class));
    	            break;
    	        case DialogInterface.BUTTON_NEGATIVE:
    	            //No button clicked
    	        	//Do nothing
    	            break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Do you want to play?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }
}