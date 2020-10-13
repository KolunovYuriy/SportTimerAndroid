package com.taktilidu.sporttimer;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;

import com.crashlytics.android.Crashlytics;
import com.taktilidu.sporttimer.common.laPublic;

import io.fabric.sdk.android.Fabric;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		laPublic.implementCrashlytics(this);
		//setContentView(R.layout.activity_preference);
		addPreferencesFromResource(R.xml.activity_preference);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
