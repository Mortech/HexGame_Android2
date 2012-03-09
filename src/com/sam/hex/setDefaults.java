package com.sam.hex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class setDefaults extends Preference implements
Preference.OnPreferenceClickListener {
	Context context;
		public setDefaults(Context context){
			super(context);
			this.context=context;
			setOnPreferenceClickListener(this);
		}
			public setDefaults(Context context, AttributeSet attrs) {
				super(context, attrs);
				this.context=context;
				setOnPreferenceClickListener(this);
			}
			
			
		

		@Override
		public boolean onPreferenceClick(Preference arg0) {
			//SharedPreferences.Editor EditSetings =Global.gamePrefs.edit();
			SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor EditSetings =prefs.edit();
			EditSetings.clear();
			EditSetings.commit();
	    	Global.playerOne = prefs.getInt("player1Color", 0xff0000ff);
	    	Global.playerTwo = prefs.getInt("player2Color", 0xffff0000);
	    	Global.board.onSizeChanged(Global.windowWidth,Global.windowHeight,Global.windowWidth,Global.windowHeight);
			Global.board.invalidate();
			return false;
		}
		
}
