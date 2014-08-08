package com.color.speechbubble;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.preference.PreferenceActivity;

public class AppPreference extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onPause(){
		super.onPause();
	}

}
