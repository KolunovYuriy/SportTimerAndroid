package com.taktilidu.sporttimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.taktilidu.sporttimer.common.PreferencesValues;
import com.taktilidu.sporttimer.common.laPublic;
import com.taktilidu.sporttimer.core.DB;

import io.fabric.sdk.android.Fabric;

import static com.taktilidu.sporttimer.core.Exercise.loadExercisesList;
import static com.taktilidu.sporttimer.core.Schedule.loadScheduleList;
import static com.taktilidu.sporttimer.core.Training.loadTrainingsList;

public class LoadPageSportTimer extends Activity {

	private ImageView ImageViewLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
		laPublic.implementCrashlytics(this);
		setContentView(R.layout.activity_load_page_sport_timer);

		//Инциализируем объект анимации. Вращающаяся спираль. 
		ImageViewLoader = (ImageView) findViewById(R.id.imageLoader);

		//Анимация//				
		Animation translate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loader);
		translate.setAnimationListener(new AnimationTranslatelistener());
		ImageViewLoader.startAnimation(translate);

		//Init Preferences
		readPreferences();

		//Init DB Objects
		InitDB();

		//Init all Exercises
		loadExercisesList();

		//Init all Trainings
		loadTrainingsList();

		//Init all Schedule
		loadScheduleList();

		//Инициализируем проставленные настройки
		if (initParams()) {
			Intent i = new Intent();
			i.setClass(this, MainActivitySportTimer.class);
			startActivity(i);
		}

		//Заствляем экран не тухнуть во время работы с приложением//
		//PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//manager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}


	private void InitDB() {
		DB db = new DB(getApplicationContext());
		db.open();
	}

	private void readPreferences() {
		PreferencesValues.reloadPreferences(getApplicationContext());
	}


	//Обработчик событий после анимации
	//*
	private class AnimationTranslatelistener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}
	//*/

	@Override
	protected void onResume() {
		super.onResume();
		finish();
	}

	private boolean initParams() {
		return true;
	}
}
