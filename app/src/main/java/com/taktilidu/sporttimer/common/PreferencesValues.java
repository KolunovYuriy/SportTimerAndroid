package com.taktilidu.sporttimer.common;

import com.taktilidu.sporttimer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;

// инициализация настроек
public class PreferencesValues {
	public static boolean isSoundNotification=true;
	public static boolean isVibrateNotification=false;
	public static int giveNoticeTime=5000;
	public static int idOfExercise=0;
	public static SharedPreferences prefs=null;
	public static Context currentContext=null;

	PreferencesValues() { }

	public static void reloadPreferences(Context context){

		currentContext = context;
		prefs =
				PreferenceManager.getDefaultSharedPreferences(context);

		Resources Res = context.getResources();

		Editor prefsEditor = prefs.edit();

		//isSoundNotification
		if (!prefs.contains(Res.getString(R.string.is_sound_notification))) {
			prefsEditor.putBoolean(Res.getString(R.string.is_sound_notification), true);
		};
		PreferencesValues.isSoundNotification = prefs.getBoolean(Res.getString(R.string.is_sound_notification),true);

		//isVibrateNotification
		PreferencesValues.isVibrateNotification = prefs.getBoolean(Res.getString(R.string.is_vibrate_notification),false);

		//giveNoticeTime
		PreferencesValues.giveNoticeTime = 1000*Integer.valueOf(prefs.getString(Res.getString(R.string.give_notice_time),"5"));

		//idOfExercise
		PreferencesValues.idOfExercise = prefs.getInt(Res.getString(R.string.id_of_current_exercise),0);

		exLog.i("Preferences", Res.getString(R.string.id_of_current_exercise)+"|||"+String.valueOf(PreferencesValues.idOfExercise));

		//Commit preference
		prefsEditor.commit();

	}

	static boolean setIdOfExercise(int l) {
		boolean result = false;
		Resources Res = currentContext.getResources();
		exLog.w("Preferences", "prefs!=null && currentContext!=null   " +String.valueOf(prefs!=null && currentContext!=null));
		exLog.i("Preferences", "setIdOfExercise=" +String.valueOf(l));
		if (prefs!=null && currentContext!=null) {
			Editor prefsEditor = prefs.edit();
			prefsEditor.putInt(Res.getString(R.string.id_of_current_exercise), l);
			result = prefsEditor.commit();
		}
		return result;
	}

}
