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
			SharedPreferences.Editor EditSetings =PreferenceManager.getDefaultSharedPreferences(context).edit();
			EditSetings.clear();
			EditSetings.commit();
			Global.board.invalidate();
			return false;
		}
		
}
