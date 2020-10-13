package com.taktilidu.sporttimer;

import java.util.ArrayList;

import com.crashlytics.android.Crashlytics;
import com.taktilidu.sporttimer.adapters.ExerciseAdapter;
import com.taktilidu.sporttimer.adapters.ExerciseItemAdapter;
import com.taktilidu.sporttimer.common.Constants;
import com.taktilidu.sporttimer.common.CustomBaseAdapter;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.common.laDialogs;
import com.taktilidu.sporttimer.common.laPublic;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.ExerciseItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.fabric.sdk.android.Fabric;

import static com.taktilidu.sporttimer.core.Exercise.reloadExercisesList;

public class ExerciseEditActivity extends Activity {

	private ArrayList<String> SelectedItems = new ArrayList<String>();
	private RelativeLayout imageAddItem;
	private RelativeLayout imageTabUp;
	private RelativeLayout imageTabDown;
	private RelativeLayout imageTabCopy;
	private RelativeLayout imageTabDelete;
	private RelativeLayout buttonCancel;
	private RelativeLayout buttonOK;

	CustomBaseAdapter currentAdapter;

	private String editionMode = Constants.SECTION_ALL_EXERCISES_MODE;
	private String mExerciseId ="";

	private DB db = DB.giveDBlink();


	Context curContext;
	Activity curActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        laPublic.implementCrashlytics(this);
		setContentView(R.layout.activity_edit_exercise);
		//заполнить режим редактирования
		Bundle extras = getIntent().getExtras();
		editionMode = extras.getString(Constants.CONST_MODE);
		exLog.d("onCreate EditTrainingExercise","editionMode = "+editionMode);
		//если модифицируются элементы конкретной сессии
		if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
			mExerciseId =getIntent().getExtras().getString(Constants.CONST_EXERCISE_ID);
		}
		//инициаизируем глобальные переменные
		curActivity = this;
		curContext = getApplicationContext();
		//заполнинть скроллер данными 
		useMatchListView();
		//инициализация событий по нажатию визуальных элементов//
		initListenerOfVisualElements();
		//старт транзакции
		db.beginTransaction();
	}

	//нажатие кнопки назад
	@Override
	public void onBackPressed() {
		db.endTransaction();
		super.onBackPressed();
	}

	//использование извне функции формирования скроллера
	public void useMatchListView(){
		SelectedItems.clear();
		ImageView IVAddEditButton = (ImageView) curActivity.findViewById(R.id.imageAddItem).findViewWithTag("buttonImageView");
		IVAddEditButton.setImageResource(R.drawable.ic_action_newcoaching_pressed);
		matchListView();
	}

	//use outside function "notifyDataSetChanged" of current adapter
	public void useNotifyDataSetChanged(){
		useNotifyDataSetChanged(true);
	}

	public void useNotifyDataSetChanged(boolean isClearSelected){
		if (isClearSelected) {
			SelectedItems.clear();
			currentAdapter.setSelectedItems(SelectedItems);
		}
		laPublic.useNotifyDataSetChanged(currentAdapter);
	}

	//формирование скроллера
	private void matchListView(){
		exLog.d("matchListView","1");
		ListView mDrawerListView = (ListView) findViewById(R.id.myTrainingList);
		LayoutInflater inflater = getLayoutInflater();
		exLog.d("matchListView","2");
		exLog.d("matchListView","editionMode="+editionMode + "   NavigationDrawerFragment.SECTION_ALL_EXERCISES_MODE="+ Constants.SECTION_ALL_EXERCISES_MODE + "   boolean1=" + (editionMode == Constants.SECTION_ALL_EXERCISES_MODE) + "   boolean2="+editionMode.compareTo(Constants.SECTION_ALL_EXERCISES_MODE));
		CustomBaseAdapter adapter = null;
		if (editionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
			exLog.d("matchListView","2.1");
			adapter = new ExerciseAdapter(curContext, inflater, SelectedItems);
		}
		else if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
			exLog.d("matchListView","2.2");
			adapter = new ExerciseItemAdapter(curContext, inflater, getIntent().getExtras().getString(Constants.CONST_EXERCISE_ID), SelectedItems);
		}
		exLog.d("matchListView","3");
		mDrawerListView.setAdapter(adapter);
		//mDrawerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		final CustomBaseAdapter finalAdapter = adapter;
		mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ImageView IVchecked = (ImageView) view.findViewById(R.id.icCheckItem);
				//*
				//AdapterItem SI = (AdapterItem) parent.getItemAtPosition(position);
				String _id="";// exercise_id / item_id
				if (editionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
					Exercise curExercise = (Exercise) parent.getItemAtPosition(position);
					_id = curExercise.getId();
				}
				else if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
					ExerciseItem curExerciseItem = (ExerciseItem) parent.getItemAtPosition(position);
					_id = curExerciseItem.getId();
				}
				exLog.i("matchListView","onItemClick contains="+SelectedItems.contains(_id));
				if (!SelectedItems.contains(_id)) {
					SelectedItems.add(_id);
					finalAdapter.setSelectedItems(SelectedItems);
					IVchecked.setImageResource(R.drawable.ic_selection_checked);
				}
				else {
					SelectedItems.remove(SelectedItems.indexOf(_id));
					finalAdapter.setSelectedItems(SelectedItems);
					IVchecked.setImageResource(R.drawable.ic_selection_unchecked);
				}
				//отображение кнопки imageAddItem
				ImageView IVAddEditButton = (ImageView) curActivity.findViewById(R.id.imageAddItem).findViewWithTag("buttonImageView");
				switch(SelectedItems.size()){
					case 1:
						IVAddEditButton.setImageResource(R.drawable.ic_action_edit_pressed);
						break;
					case 0:
					default:
						IVAddEditButton.setImageResource(R.drawable.ic_action_newcoaching_pressed);
						break;
				}
			}
		});

		currentAdapter = adapter;

	}

	//навешиваем события на кнопки
	private void initListenerOfVisualElements() {
		exLog.i("initListenerOfVisualElements","init");
		ImageView IV;
		TextView TV;
		View.OnClickListener OnCL;
		View.OnTouchListener OnTL;

		OnTL = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // нажатие
						v.setBackgroundResource(R.color.grey_custom_medium_color);
						break;
					case MotionEvent.ACTION_MOVE: // движение
						break;
					case MotionEvent.ACTION_UP: // отпускание
					case MotionEvent.ACTION_CANCEL:
						v.setBackgroundResource(R.color.white_color);
						break;
				}
				return false;
			}
		};
		//добавить/редактировать//
		exLog.i("initListenerOfVisualElements","init imageAddItem");
		//*
		imageAddItem = (RelativeLayout)(findViewById(R.id.imageAddItem));
		IV = (ImageView) (findViewById(R.id.imageAddItem)).findViewWithTag("buttonImageView");
		OnCL = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch(SelectedItems.size()){
					case 1:
						if (editionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
							laDialogs.showDialogEditExercise(curActivity, laDialogs.ADD_EDIT_EXERCISE_DIALOG, (String)SelectedItems.toArray()[0]);//
						}
						else if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
							laDialogs.showDialogAddItemExercise(curActivity, laDialogs.ADD_EDIT_ITEM_EXERCISE_DIALOG, mExerciseId,(String)SelectedItems.toArray()[0]);
						}
						break;
					case 0:
					default:
						if (editionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
							laDialogs.showDialogAddExercise(curActivity, laDialogs.ADD_EDIT_EXERCISE_DIALOG);
						}
						else if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
							laDialogs.showDialogAddItemExercise(curActivity, laDialogs.ADD_EDIT_ITEM_EXERCISE_DIALOG, mExerciseId);
						}
						break;
				}
				useNotifyDataSetChanged();
				exLog.i("initListenerOfVisualElements","imageAddItem");
			}
		};
		imageAddItem.setOnClickListener(OnCL);
		imageAddItem.setOnTouchListener(OnTL);
		//*/
		//переместить вверх//
		exLog.i("initListenerOfVisualElements","init imageTabUp");
		//*
		imageTabUp = (RelativeLayout)(findViewById(R.id.imageTabUp));
		IV = (ImageView) ((RelativeLayout)(findViewById(R.id.imageTabUp))).findViewWithTag("buttonImageView");
		OnCL = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				exLog.i("initListenerOfVisualElements","imageTabUp");
				if (SelectedItems.size()!=0) changeElementsOrder(DB.MOVE_ELEMENT_UP);
			}
		};
		imageTabUp.setOnClickListener(OnCL);
		imageTabUp.setOnTouchListener(OnTL);
		//*/
		//переместить вниз//
		exLog.i("initListenerOfVisualElements","init imageTabDown");
		imageTabDown = (RelativeLayout)(findViewById(R.id.imageTabDown));
		IV = (ImageView) ((RelativeLayout)(findViewById(R.id.imageTabDown))).findViewWithTag("buttonImageView");
		OnCL = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				exLog.i("initListenerOfVisualElements","imageTabDown");
				if (SelectedItems.size()!=0) changeElementsOrder(DB.MOVE_ELEMENT_DOWN);
			}
		};
		imageTabDown.setOnClickListener(OnCL);
		imageTabDown.setOnTouchListener(OnTL);
		//копировать//
		exLog.i("initListenerOfVisualElements","init imageTabCopy");
		imageTabCopy = (RelativeLayout)(findViewById(R.id.imageTabCopy));
		IV = (ImageView) ((RelativeLayout)(findViewById(R.id.imageTabCopy))).findViewWithTag("buttonImageView");
		OnCL = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				exLog.i("initListenerOfVisualElements","imageTabCopy");
				CopyExercises();
			}
		};
		imageTabCopy.setOnClickListener(OnCL);
		imageTabCopy.setOnTouchListener(OnTL);
		//удалить//
		exLog.i("initListenerOfVisualElements","init imageTabDelete");
		imageTabDelete = (RelativeLayout)(findViewById(R.id.imageTabDelete));
		IV = (ImageView) ((RelativeLayout)(findViewById(R.id.imageTabDelete))).findViewWithTag("buttonImageView");
		OnCL = new View.OnClickListener() {

			@Override
			public void onClick(View v) {//TODO
				exLog.i("initListenerOfVisualElements","Delete, SelectedItems.size="+SelectedItems.size());
				if (SelectedItems.size()!=0) {
					laDialogs.showDialogDeleteItem(curActivity, laDialogs.DELETE_EXERCISE_DIALOG, mExerciseId, editionMode, SelectedItems);//TODO: переделать через обращение к currentAdapter. Чтобы динамически перерисовывать картинку
					//SelectedItems.clear();
					//currentAdapter.setSelectedItems(SelectedItems);
					//currentAdapter.notifyDataSetChanged();
					//currentAdapter.notifyDataSetInvalidated();
				}
			}
		};
		imageTabDelete.setOnClickListener(OnCL);
		imageTabDelete.setOnTouchListener(OnTL);
		//для кнопок приенения//
		OnTL = new View.OnTouchListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//*
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // нажатие
						v.setBackgroundResource(R.color.red_custom_color);
						((TextView) v.findViewWithTag("buttonImageView")).setTextColor(getResources().getColor(R.color.white_color));
						break;
					case MotionEvent.ACTION_MOVE: // движение
						break;
					case MotionEvent.ACTION_UP: // отпускание
					case MotionEvent.ACTION_CANCEL:
						v.setBackgroundResource(R.color.white_color);
						((TextView) v.findViewWithTag("buttonImageView")).setTextColor(getResources().getColor(R.color.blue_custom_color));
						break;
				}
				//*/
				return false;
			}
		};
		//отмена//
		exLog.i("initListenerOfVisualElements","init buttonCancel");
		buttonCancel = (RelativeLayout)(findViewById(R.id.buttonCancel));
		OnCL = new View.OnClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				exLog.i("initListenerOfVisualElements","buttonCancel");
				//действие
				Intent result = new Intent();
				setResult(RESULT_CANCELED, result);
				onBackPressed();
			}
		};
		buttonCancel.setOnClickListener(OnCL);
		buttonCancel.setOnTouchListener(OnTL);
		//готово//
		exLog.i("initListenerOfVisualElements","init buttonOK");
		buttonOK = (RelativeLayout)(findViewById(R.id.buttonOK));
		OnCL = new View.OnClickListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				exLog.i("initListenerOfVisualElements","buttonOK");
				//действие		
				db.setTransactionSuccessful();
				reloadExercisesList();
				Intent result = new Intent();
				setResult(RESULT_OK, result);
				onBackPressed();
			}
		};
		buttonOK.setOnClickListener(OnCL);
		buttonOK.setOnTouchListener(OnTL);
	}

	// копируем сессии //
	private void CopyExercises() {//TODO: переделать через обращение к currentAdapter. Чтобы динамически перерисовывать картинку
		exLog.i("CopyExercises","1");
		Object[] ArrSI = SelectedItems.toArray();
		exLog.i("CopyExercises","2, ArrSI.length="+ArrSI.length);
		for(int i=0;i<ArrSI.length;i++)
		{
			exLog.i("CopyExercises","i="+i);
			if (editionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
				Exercise.copy(String.valueOf(ArrSI[i]));
			}
			else if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
				ExerciseItem.copy(String.valueOf(ArrSI[i]), mExerciseId);
			}
		}
		exLog.i("CopyExercises","3");
		useNotifyDataSetChanged();
	}

	// перемещаем элемент вверх
	private void changeElementsOrder(String moveDirect) {
		String ids = "";
		exLog.i("changeElementsOrder","1");
		Object[] ArrSI = SelectedItems.toArray();
		exLog.i("changeElementsOrder","2");
		for(int i=0;i<ArrSI.length;i++)
		{
			exLog.i("changeElementsOrder","i="+i);
			ids=ids+ArrSI[i]+",";
		}
		exLog.i("changeElementsOrder1, 1 ids="+ids);
		if (ids.length()>0) ids = ids.subSequence(0,ids.length()-1).toString();
		exLog.i("changeElementsOrder1, 2 ids="+ids);
		//ids=ids+"0";
		String sTable="";
		if (editionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
			Exercise.changeElementsOrder(ids,moveDirect);
		}
		else if (editionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
			ExerciseItem.changeElementsOrder(ids,String.valueOf(mExerciseId),moveDirect);
		}
		exLog.i("changeElementsOrder","3");
		//SelectedItems.clear();
		//matchListView();
		useNotifyDataSetChanged(false);
	}

}
