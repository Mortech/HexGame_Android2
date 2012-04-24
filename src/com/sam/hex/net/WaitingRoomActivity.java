package com.sam.hex.net;

import java.util.LinkedList;

import com.sam.hex.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class WaitingRoomActivity extends Activity {
	private static LinkedList<String> messages = new LinkedList<String>(); 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitingroom);

    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	final String username = prefs.getString("netUsername", "");
        Button home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	EditText text = (EditText) findViewById(R.id.sendMessage);
            	messages.add(username+": "+text.getText().toString());
            	text.setText("");
            	refreshMessages();
            }
        });
        
        EditText text = (EditText) findViewById(R.id.sendMessage);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId==EditorInfo.IME_ACTION_DONE || event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
					messages.add(username+": "+v.getText().toString());
	            	v.setText("");
	            	refreshMessages();
				}
				return false;
			}
		});
        
    	refreshMessages();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
    
    private void refreshMessages(){
    	TextView messageBoard = (TextView) findViewById(R.id.messages);
    	String msg = "";
    	for(int i=0;i<messages.size();i++){
    		msg+=messages.get(i)+"\n";
    	}
    	messageBoard.setText(msg);
    	final ScrollView sv = (ScrollView) findViewById(R.id.messageScroller);
    	sv.post(new Runnable() {            
    	    @Override
    	    public void run() {
    	           sv.fullScroll(View.FOCUS_DOWN);              
    	    }
    	});

    }
}