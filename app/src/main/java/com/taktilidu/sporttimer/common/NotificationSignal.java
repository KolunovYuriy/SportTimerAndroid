package com.taktilidu.sporttimer.common;

import com.taktilidu.sporttimer.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;

// инициализация настроек
public class NotificationSignal {
	private NotificationManager mNotificationManager;
	private Notification notification_short, notification_long;
	private static final int NOTIFY_ID = 1;
	long[] vibrate = new long[] { 0, 200 };
	private MediaPlayer player_for_short_signal, player_for_long_signal;

	public NotificationSignal(Context context){
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notification_short = new Notification();
		notification_short.vibrate = vibrate;
		//											
		notification_long = new Notification();
		notification_long.defaults |= Notification.DEFAULT_VIBRATE;
		//Media
		player_for_short_signal = MediaPlayer.create(context, R.raw.cens_short);
		player_for_long_signal = MediaPlayer.create(context, R.raw.cens_long);

		//player_for_short_signal = MediaPlayer.create(context, R.raw.w_default_short);
		//player_for_long_signal = MediaPlayer.create(context, R.raw.w_default_short);
	}

	public void shortSignal() {
		if (PreferencesValues.isVibrateNotification) {shortVibrateSignal();}
		if (PreferencesValues.isSoundNotification) {shortSoundSignal();}
		exLog.i("shortSignal","|||"+String.valueOf(PreferencesValues.isSoundNotification)+"|||"+String.valueOf(PreferencesValues.isVibrateNotification));
	}

	public void shortSignal(boolean isSound) {
		if (PreferencesValues.isVibrateNotification) {shortVibrateSignal();}
		if (isSound) {shortSoundSignal();}
		exLog.i("shortSignal 2","|||"+String.valueOf(PreferencesValues.isSoundNotification)+"|||"+String.valueOf(PreferencesValues.isVibrateNotification));
	}

	public void longSignal() {
		if (PreferencesValues.isVibrateNotification) {longVibrateSignal();}
		if (PreferencesValues.isSoundNotification) {longSoundSignal();}
	}

	public void longSignal(boolean isSound) {
		if (PreferencesValues.isVibrateNotification) {longVibrateSignal();}
		if (isSound) {longSoundSignal();}
	}

	public void shortVibrateSignal() {
		try {
			mNotificationManager.notify(NOTIFY_ID, notification_short);
		}
		catch (Exception e) {}
	}

	public void longVibrateSignal() {
		try {
			mNotificationManager.notify(NOTIFY_ID, notification_long);
		}
		catch (Exception e) {}
	}

	public void shortSoundSignal() {
		player_for_short_signal.start();
	}

	public void longSoundSignal() {
		player_for_long_signal.start();
	}

	//---------------------------//
}
