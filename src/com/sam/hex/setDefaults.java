package com.sam.hex;

import android.content.Context;
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
		//Clear everything
		PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
		notifyChanged();
		return false;
	}
}
