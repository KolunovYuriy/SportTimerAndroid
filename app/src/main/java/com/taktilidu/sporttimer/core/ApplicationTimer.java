package com.taktilidu.sporttimer.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.taktilidu.sporttimer.adapters.ExerciseDragItemAdapter;
import com.taktilidu.sporttimer.common.NotificationSignal;
import com.taktilidu.sporttimer.common.PreferencesValues;
import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laPublic;
import com.taktilidu.sporttimer.view.CircleTimerView;
import com.woxthebox.draglistview.DragListView;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.MotionEvent;///////////////////
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ApplicationTimer {

	// Default time value
	private long oneHour = 60*60*1000;
	private long oneMinute = 60*1000;
	private long oneSecond = 1000;

	//  Parameters
	private Context currContext;
	private View mDrawerList;
	private DragListView mDragListView;
	private TextView textViewMainTimer;
	private CircleTimerView mCircleTimerView;
	private ExerciseItem mExerciseItem;
	private Exercise mExercise;
	private ImageView imagePause;
	private ImageView imageStop;
	private ImageView imageSaveTime;
	private ImageView imageSound;
	private ImageView imageRepeat;

	private ExerciseDragItemAdapter mAdapter;
	//flags//
	boolean isPause=false;
	boolean isStart=false;
	boolean isSound=false;
	boolean isRepeat=false;

	boolean isAlarmSignal=false;
	//-----//
	private long mTime = 0;
	private long mTimeToSave = 0;
	private int mPosition = 0;
	//-----//
	private CountDownTimer MainTimer;
	private long mUntilFinished=0;
	private NotificationSignal NS;
	//-----//
	ViewGroup curContainer;

	public ApplicationTimer(Context c, ViewGroup container, View v, ExerciseDragItemAdapter adapter, Exercise exercise){
		currContext = c;
		mDrawerList = v;
		curContainer = container;
		mDragListView = (DragListView) mDrawerList.findViewById(R.id.myTrainingList);
		mCircleTimerView = (CircleTimerView) mDrawerList.findViewById(R.id.circletimerview);
		mAdapter = adapter;
		mExercise = exercise;
		//Create notificator manager instance
		NS = new NotificationSignal(currContext);
		//use default values//
		setCurrentTime();
		mCircleTimerView.setTime(mTime);
		mCircleTimerView.setCentralText(currContext.getResources().getText(R.string.start_button).toString());
		//mCircleTimerView.setTimeFormat(CircleTimerView.SHORT_TIME_FORMAT);

		//pause button//
		imagePause = (ImageView) mDrawerList.findViewById(R.id.imagePause);
		imagePause.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Pause(v);
			}

		});

		imagePause.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // нажатие
						if (!isPause)
							((ImageView) v).setImageResource(R.drawable.ic_pause_pressed2);
						else ((ImageView) v).setImageResource(R.drawable.ic_pause);
						break;
					case MotionEvent.ACTION_MOVE: // движение
						break;
					case MotionEvent.ACTION_UP: // отпускание
					case MotionEvent.ACTION_CANCEL:
						if (isPause)
							((ImageView) v).setImageResource(R.drawable.ic_pause_pressed2);
						else ((ImageView) v).setImageResource(R.drawable.ic_pause);
						break;
				}
				return false;
			}

		});

		//stop button//
		imageStop = (ImageView) mDrawerList.findViewById(R.id.imageStop);
		imageStop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mAdapter.setSelectedRow(mPosition);
				mPosition =0;
				Stop();
			}

		});

		imageStop.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // нажатие
						((ImageView) v).setImageResource(R.drawable.ic_stop_pressed2);
						break;
					case MotionEvent.ACTION_MOVE: // движение
						break;
					case MotionEvent.ACTION_UP: // отпускание
					case MotionEvent.ACTION_CANCEL:
						((ImageView) v).setImageResource(R.drawable.ic_stop);
						break;
				}
				return false;
			}

		});

		// init Start button
		mCircleTimerView.setOnClickCenterCircleListener(new CircleTimerView.OnClickCenterCircleListener() {
			@Override
			public void onClick(View v) {
				exLog.i("OnClickCenterCircleListener");
				//if (isPause) {Pause(imagePause);} else {Start(v);}
				if (!isStart) {Start(v);} else {Pause(imagePause);}
			}
		});//

		imageSaveTime = (ImageView) mDrawerList.findViewById(R.id.saveTime);
		imageSaveTime.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mExerciseItem = (ExerciseItem) mAdapter.getItem(mPosition);
				mExerciseItem.setTime((mTimeToSave/oneSecond)*oneSecond);
				mAdapter.setSelectedRow(mPosition);
				mAdapter.notifyDataSetChanged();
				//mDragListView.smoothScrollToPositionFromTop(mPosition, 0, 500); // TODO: переделать перемещение
				mDragListView.setVerticalScrollbarPosition(mPosition);
				if (!isStart) setCurrentTime();
				hideSaveButton();

			}
		});

		mCircleTimerView.setOnTimeChangedListener(new CircleTimerView.OnTimeChangedListener() {
			@Override
			public void onChange(View v) {
				exLog.i("OnTimeChangedListener onChange");
				unhideSaveButton();
				//((TextView) mDrawerList.findViewById(R.id.curr_item_time)).setText(((CircleTimerView) v).getSTime(CircleTimerView.SHORT_TIME_FORMAT));
				((TextView) mDrawerList.findViewById(R.id.curr_item_time)).setText(laPublic.TimeFormatShort(mTimeToSave));
				if (!isPause) {Pause(imagePause);}
			}

			@Override
			public void onActionUp(View v) {
				exLog.i("OnTimeChangedListener onActionUp");
				mTime = ((CircleTimerView) v).getTime();//TODO: доделать
				mUntilFinished = mTime;
			}
		});

		//voice button//
		imageSound = (ImageView) mDrawerList.findViewById(R.id.imageSound);
		//if (!PreferencesValues.isSoundNotification) {
		if(!mExercise.getSound()) {
			isSound=false;
			imageSound.setImageResource(R.drawable.ic_music);
		}
		else {
			isSound=true;
			imageSound.setImageResource(R.drawable.ic_music_pressed2);
		}
		imageSound.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Sound(v);
			}

		});

		imageSound.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // нажатие
						if (!isSound)
							((ImageView) v).setImageResource(R.drawable.ic_music_pressed2);
						else ((ImageView) v).setImageResource(R.drawable.ic_music);
						break;
					case MotionEvent.ACTION_MOVE: // движение
						break;
					case MotionEvent.ACTION_UP: // отпускание
					case MotionEvent.ACTION_CANCEL:
						if (isSound)
							((ImageView) v).setImageResource(R.drawable.ic_music_pressed2);
						else ((ImageView) v).setImageResource(R.drawable.ic_music);
						break;
				}
				return false;
			}

		});

		//repeat button//
		imageRepeat = (ImageView) mDrawerList.findViewById(R.id.imageRepeat);
		if(!mExercise.getRepeat()) {
			isRepeat=false;
			imageRepeat.setImageResource(R.drawable.ic_repeat);
		}
		else {
			isRepeat=true;
			imageRepeat.setImageResource(R.drawable.ic_repeat_pressed2);
		}
		imageRepeat.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Repeat(v);
			}

		});

		imageRepeat.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // нажатие
						if (!isRepeat)
							((ImageView) v).setImageResource(R.drawable.ic_repeat_pressed2);
						else ((ImageView) v).setImageResource(R.drawable.ic_repeat);
						break;
					case MotionEvent.ACTION_MOVE: // движение
						break;
					case MotionEvent.ACTION_UP: // отпускание
					case MotionEvent.ACTION_CANCEL:
						if (isRepeat)
							((ImageView) v).setImageResource(R.drawable.ic_repeat_pressed2);
						else ((ImageView) v).setImageResource(R.drawable.ic_repeat);
						break;
				}
				return false;
			}

		});

		//------------//

		//create cursor
		//Activity a = (Activity) currContext;
		RelativeLayout RL = (RelativeLayout) mDrawerList.findViewById(R.id.timerLayout);
		//RL.addView(new TimePointerView(currContext));
		//RL.addView(new CircleTimerView(currContext));

	}

	//begin of training//
	private void Start(View v) {
		if (isStart) {
			isStart=true;
		}
		else {
			mCircleTimerView.setTime(mTime);
			mCircleTimerView.setTimerNumberColor(currContext.getResources().getColor(R.color.white_color));
			mCircleTimerView.setIsStart(true);
			isStart=true;
			if (mTime !=0)
				goTimer();
		}
	}

	//run timer
	private void goTimer() {
		//MainTimerStop();//TODO
		mCircleTimerView.setCentralText(currContext.getResources().getText(R.string.stop_button).toString());
		exLog.i("goTimer","mTime = "+Long.toString(mTime));
		MainTimer = new CountDownTimer(mTime, 1) {//TODO сделать настраиваюемую задержку
			public void onTick(long millisUntilFinished) {
				//record remaining time until the end of the element exercise//
				mUntilFinished=millisUntilFinished;
				//Draw the remaining time//
				mCircleTimerView.setTime(millisUntilFinished);
				//textViewMainTimer.setText(TimeFormat(millisUntilFinished));
				if ( (PreferencesValues.giveNoticeTime > millisUntilFinished) && !isAlarmSignal ) {
					if (!isAlarmSignal) {
						isAlarmSignal=true;
						mCircleTimerView.setTimerNumberColor(currContext.getResources().getColor(R.color.red_custom_color));
						try {
							StartShortSignal();
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}//if !isAlarmSignal
				}//if
			}

			public void onFinish() {
				exLog.i("onFinish","isPause = "+Boolean.toString(isPause));
				if (!isPause)
				{
					//textViewMainTimer.setTextColor(currContext.getResources().getColor(R.color.white));//
					mCircleTimerView.setTimerNumberColor(currContext.getResources().getColor(R.color.white_color));
					isAlarmSignal=false;
					LongSignal();
					goNextTime();//
				}
				//Animation// TODO
			    	/*
					Animation translate1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate2);
					Animation translate2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
					
					translate1.setAnimationListener(new AnimationTranslatelistener());
					translate2.setAnimationListener(new AnimationTranslatelistener());
					
					TextViewtimer1.startAnimation(translate1);
					TextViewtimer2.startAnimation(translate2);								
					*/
			}

			public void drop() {
				this.cancel();
				try {
					this.finalize();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		MainTimer.start();
	}

	private void MainTimerStop() {
		if(MainTimer != null) { MainTimer.cancel(); MainTimer=null;}
	}

	//the next element of training
	private void goNextTime() {
		mPosition++;
		if(mPosition != mExercise.listOfExerciseItems.size()) {
			setCurrentTime();
			hideSaveButton();
			goTimer();
		}
		else
		{
			mPosition =0;
			setCurrentTime();
			if (isRepeat) {
				hideSaveButton();
				goTimer();
			}
			else {
				isStart=true;
				Stop();
			}
		}
	}

	//Start serial of short signal//
	public void StartShortSignal() throws Throwable {
		final int num_of_signal = (int) (PreferencesValues.giveNoticeTime/1000);
		//static int cur_num_of_signal = 0;
		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			int cur_num_of_signal = 0;
			public void run() {
				cur_num_of_signal++;
				if (MainTimer==null) {this.cancel();}
				ShortSignal();
				if (cur_num_of_signal==num_of_signal) {this.cancel();}
			}
		};
		timer.schedule(task,0,1000);

	}
	//----------------------------//

	//Short Signal//
	private void ShortSignal() {
		Thread t = new Thread(){
			public void run(){
				NS.shortSignal(isSound);
			}};
		t.start();
	}
	//Short Signal//
	//Long  Signal//
	private void LongSignal() {
		Thread t = new Thread(){
			public void run(){
				NS.longSignal(isSound);
			}};
		t.start();
	}
	//------------//

	//-----------------//

	private void Pause(View v) {
		ImageView IV = (ImageView) v;
		if (isStart) {
			if (isPause) {
				mTime =mUntilFinished;
				goTimer();
				IV.setImageResource(R.drawable.ic_pause);
				mCircleTimerView.setIsStart(true);
				isPause=false;
			}
			else {
				MainTimerStop();
				IV.setImageResource(R.drawable.ic_pause_pressed2);
				mCircleTimerView.setCentralText(currContext.getResources().getText(R.string.start_button).toString());
				mCircleTimerView.setIsStart(false);
				isPause=true;
			}
		}
		else {
			IV.setImageResource(R.drawable.ic_pause);
			mCircleTimerView.setIsStart(false);
			isPause=false;
		}
	}

	private void Sound(View v) {
		ImageView IV = (ImageView) v;
		if (isSound) {
			isSound=false;
			//PreferencesValues.prefs.edit().putBoolean(currContext.getResources().getString(R.string.is_sound_notification), false);
			//PreferencesValues.isSoundNotification=false;
			IV.setImageResource(R.drawable.ic_music);
		}
		else {
			isSound=true;
			//PreferencesValues.prefs.edit().putBoolean(currContext.getResources().getString(R.string.is_sound_notification), true);
			//PreferencesValues.isSoundNotification=true;
			IV.setImageResource(R.drawable.ic_music_pressed2);
		}
		mExercise.setSound(isSound);
	}

	private void Repeat(View v) {
		ImageView IV = (ImageView) v;
		if (isRepeat) {
			isRepeat=false;
			IV.setImageResource(R.drawable.ic_repeat2);
		}
		else {
			isRepeat=true;
			IV.setImageResource(R.drawable.ic_repeat_pressed2);
		}
		mExercise.setRepeat(isRepeat);
	}

	private void Stop() {
		MainTimerStop();
		setCurrentTime();
		hideSaveButton();
		mCircleTimerView.setCentralText(currContext.getResources().getText(R.string.start_button).toString());
		mCircleTimerView.setTimerNumberColor(currContext.getResources().getColor(R.color.white_color));
		isStart=false;
		Pause(imagePause);
		isPause=false;
		//ImageView IV = (ImageView) v;
		//IV.setImageResource(R.drawable.ic_);
	}

	//choose the position
	public void selectPosition(int position) {
		mAdapter.setSelectedRow(mPosition);
		mPosition = position;
		Stop();
	}

	//Time Format for application//
	private String TimeFormat(long t) {
		String format = "HH:mm:ss.SSS";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date(t));
	}
	//to put the current time according to the current position//
	private void setCurrentTime() {
		hideSaveButton();
		mExerciseItem = (ExerciseItem) mAdapter.getItem(mPosition);
		mAdapter.setSelectedRow(mPosition);
		//mDragListView.smoothScrollToPositionFromTop(mPosition, 0, 500);// TODO: переделать перемещение
		mDragListView.setVerticalScrollbarPosition(mPosition);
		//if (mExerciseItem.getRowView()!=null)((ImageView) mExerciseItem.getRowView().findViewById(R.id.icPosition)).setImageResource(R.drawable.ic_timer_e);
		//((ImageView) adapter.getView(mPosition,null,curContainer).findViewById(R.id.icPosition)).setImageResource(R.drawable.ic_timer_e);
		mTime = mExerciseItem.getlTime();
		mCircleTimerView.setTime(mTime);
		((TextView) mDrawerList.findViewById(R.id.curr_item_name)).setText(mExerciseItem.getName());
		((TextView) mDrawerList.findViewById(R.id.curr_item_time)).setText(mExerciseItem.getSTime());
		exLog.i("setCurrentTime", "mPosition="+ mPosition);
	}
	//To put at the top the value of the current element exercise//
	private void setCurrentTimeLine() {

	}

	//-----//
	public void setCurrentPosition(int position) {
		mPosition = position;
	}


		void hideSaveButton() {
		if (null != imageSaveTime)
			imageSaveTime.setVisibility(View.GONE);
	}

	void unhideSaveButton() {
		if (null != imageSaveTime) {
			imageSaveTime.setVisibility(View.VISIBLE);
			mTimeToSave = Math.round((float)(mCircleTimerView.getTime())/oneSecond)*oneSecond;
		}
	}

	//---------------------------//
}
