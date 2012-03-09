package com.sam.hex;

import android.app.Activity;
import android.os.Bundle;

public class Instructions extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
}