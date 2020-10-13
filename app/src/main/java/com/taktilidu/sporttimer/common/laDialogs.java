package com.taktilidu.sporttimer.common;

import java.util.ArrayList;

import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.ExerciseEditActivity;
import com.taktilidu.sporttimer.IntervalTimerActivity;
import com.taktilidu.sporttimer.MainActivitySportTimer;
import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.ExerciseItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

public class laDialogs {

	private static Dialog dialogMain = null;
	private static	AlertDialog dialogEdit;
	private static AlertDialog dialogAdd;
	private static LinearLayout DialogView = null;
	private static LinearLayout DialogEditView=null;
	private static LinearLayout DialogAddView=null;
	public static final int ADD_EDIT_EXERCISE_DIALOG = 1001;
	public static final int ADD_EDIT_ITEM_EXERCISE_DIALOG = 1002;
	public static final int DELETE_EXERCISE_DIALOG = 1003;
	public static Context currContext;
	public static Activity currActivity;
	public static String currExerciseId;
	public static String currExerciseItemId;
	public static String currEditionMode;
	private static DB db = DB.giveDBlink();
	private static ArrayList<String> currSelectedItems = new ArrayList<String>();

	private static void initGlobalAttribute(Context c, Activity a) {
		currContext = c;
		currActivity = a;
	}

	public static void showDialogAddExercise(Context c, int dialogType) {
		//((Activity) c).showDialog(dialogType);
		initGlobalAttribute(c,((Activity) c));
		currExerciseId="";
		onCreateDialogEx(dialogType).show();
	}

	public static void showDialogEditExercise(Context c, int dialogType, String exerciseId) {
		initGlobalAttribute(c,((Activity) c));
		currExerciseId = exerciseId;
		onCreateDialogEx(dialogType).show();
	}

	public static void showDialogAddItemExercise(Context c, int dialogType, String exerciseId) {
		initGlobalAttribute(c,((Activity) c));
		currExerciseId = exerciseId;
		currExerciseItemId = "";
		onCreateDialogEx(dialogType).show();
	}

	public static void showDialogAddItemExercise(Context c, int dialogType, String exerciseId, String ExerciseItemId) {
		initGlobalAttribute(c,((Activity) c));
		currExerciseId = exerciseId;
		currExerciseItemId = ExerciseItemId;
		onCreateDialogEx(dialogType).show();
	}

	public static void showDialogDeleteItem(Context c, int dialogType, String exerciseId, String editionMode, ArrayList<String> SelectedItems) {
		initGlobalAttribute(c,((Activity) c));
		currExerciseId = exerciseId;
		currEditionMode = editionMode;
		currSelectedItems = SelectedItems;
		exLog.i("showDialogDeleteItem","SelectedItems = " + SelectedItems.toString());
		onCreateDialogEx(dialogType).show();
	}

	protected static Dialog onCreateDialogEx(int id) {
		switch (id) {
			case ADD_EDIT_EXERCISE_DIALOG:
				return InitDialogExercise();
			case ADD_EDIT_ITEM_EXERCISE_DIALOG:
				return InitDialogItemExercise(false);
			case DELETE_EXERCISE_DIALOG:
				return DeleteExercisesDialog();
			default:
				return null;
		}
	}

	private Dialog InitDialogAddItem() {
		if (DialogAddView!=null){
			InitDialogDefaultValues(DialogAddView);
		}
		return InitDialogItemExercise(false);
	}

	private Dialog InitDialogEditItem() {
		return InitDialogItemExercise(true);
	}

	// Проверка заполнения диалога создания сессии

	private static boolean CheckDialogValues(View v, String name){
		boolean Result = true;
		if (name.equals("")) {
			Result = false;
		}
		return Result;
	}

	// Проверка заполнения диалога создания элемента сессии

	private static boolean CheckDialogValues(View v, long time, String name){
		boolean Result = true;
		if (name.equals("")) {
			//EditText et;
			//et = (EditText) DialogView.findViewById(R.id.item_name);
			//et.setFocusable(true);
			Result = false;
		}
		else if (time == 0) {
			Result = false;
		}
		return Result;
	}

	// Получить id помеченного элемента
	private static String getItemId() {//TODO: переделать. Сейчас возвращает id выбранной сессии
		//TextView item_tv = (TextView) CurrentView.findViewById(R.id.item_id);
		//return Integer.valueOf((String)item_tv.getText());
		return currExerciseItemId;
	}

	public static void showSoftKeyboardOnView(final View view) {
		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				view.requestFocus();
				// Yes, I know what you are thinking about that. If you knew something better by any chance it would be magnificent to have your idea here in code.
				view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
				view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
			}
		}, 200);
	}
	public static void hideSoftKeyboardOnView(View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	//обработка кнопки Cancel диалога
	private static View.OnClickListener onClickLisenerCancel = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			//Dialog d = (Dialog) v.getParent();
			//d.cancel();
			if (dialogMain!=null) dialogMain.cancel();
			if (dialogEdit!=null) dialogEdit.cancel();
			if (dialogAdd!=null) dialogAdd.cancel();
		}
	};

	//Инициировать диалог создания новой сессии
	private static Dialog InitDialogExercise() {
		//exLog.i("InitDialogExercise", "start");
		AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
		// создаем view из dialog.xml
		LinearLayout view = (LinearLayout) currActivity.getLayoutInflater()
				.inflate(R.layout.add_edit_exercise, null);
		// устанавливаем ее, как содержимое тела диалога
		builder.setView(view);
		//вешаем события на кнопки
		// Save
		final Button OkButton = (Button) view.findViewById(R.id.ok_item_exercise_button);
		OkButton.setOnClickListener(
				new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						EditText et;
						et = (EditText) DialogView.findViewById(R.id.item_name);
						String item_name = et.getText().toString();

						if (CheckDialogValues(DialogView,item_name)) {
							if (currExerciseId.length()>0) {
								Exercise.updateExerciseName(String.valueOf(currExerciseId),item_name);
								//db.updateExerciseName(String.valueOf(currExerciseId),item_name);
							}
							else {
								Exercise.addExercise(item_name);
								//String id = db.insertExercise(item_name);
							}
							//FragmentManager fragmentManager = getFragmentManager();
							//fragmentManager.beginTransaction().replace(R.id.container,PlaceholderFragment.newInstance(SECTION_ALL_EXERCISES_MODE,this)).commit();
		    	    			/*
		    					Intent i = new Intent();
		    					i.setClass(getApplicationContext(), ExerciseSettingsActivity.class);
		    					//Regime
		    					i.putExtra("Regime", REGIM_ADD);
		    					//Exercise Id
		    					i.putExtra("Exercise Id", id);
		    		            startActivity(i);
		    	    			//*/
							//dialogMain.cancel();
							exLog.i("OkButton.setOnClickListener","currContext.toString()"+currContext.toString());
							if (currContext.toString().contains("ExerciseEditActivity"))
							{
								//((ExerciseEditActivity) currContext).useMatchListView();
								((ExerciseEditActivity) currContext).useNotifyDataSetChanged();
							}
							else if (currContext.toString().contains("MainActivitySportTimer"))
							{
								((MainActivitySportTimer) currContext).useNotifyDataSetChanged();
								//((NavigationDrawerFragment.NavigationDrawerCallbacks) currContext).onNavigationDrawerItemSelected(NavigationDrawerFragment.SECTION_ALL_EXERCISES_MODE);//0
							}
							dialogMain.cancel();
						}
						//InitActivity();
					}
				}

		);
		// Cancel
		final Button CancelButton = (Button) view.findViewById(R.id.cancel_item_exercise_button);
		CancelButton.setOnClickListener(onClickLisenerCancel);
		// EditText
		final EditText editText= (EditText) view.findViewById(R.id.item_name);

		if (currExerciseId.length()>0) {
			/*
			db.getTrainingExerciseData(currExerciseId);
			Cursor c =db.getTrainingExerciseData(currExerciseId);
			String s = "";
			exLog.i("InitDialogExercise", "currExerciseId="+currExerciseId);
			if (c.moveToFirst()) {
				exLog.i("InitDialogExercise", "Find exercise with id = "+currExerciseId);
				s=c.getString(c.getColumnIndex(db.COLUMN_EXERCISE_NAME));
				//editText.setText(c.getString(c.getColumnIndex(db.COLUMN_EXERCISE_NAME));
			}
			else {
				exLog.i("InitDialogExercise", "Not find exercise with id = "+currExerciseId);
			}
			//*/
			InitDialogEditValues(view, Exercise.mapOfExercises.get(String.valueOf(currExerciseId)).getName());
		}
		else
		{
			InitDialogAddValues(view);
		}

		showSoftKeyboardOnView(editText);

		//------------------------//
		builder.setCancelable(false);
		DialogView = view;
		dialogMain = builder.create();
		//exLog.i("InitDialogExercise", "end");
		return dialogMain;
	}

	private static void InitDialogAddValues(LinearLayout v) {
		EditText et;
		et = (EditText) v.findViewById(R.id.item_name);
		et.setText("");
	}

	// Инициализация диалога создания/изменения записи
	private static void InitDialogEditValues(LinearLayout v, String s) {
		EditText et;
		et = (EditText) v.findViewById(R.id.item_name);
		et.setText(s);
	}

	//Инициировать диалог создания нового элемента сессии
	private static Dialog InitDialogItemExercise(boolean isEdit){


		AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
		// создаем view из add_edit_exercise.xml
		LinearLayout view = (LinearLayout) currActivity.getLayoutInflater()
				.inflate(R.layout.add_edit_exercise_item, null);
		// устанавливаем ее, как содержимое тела диалога
		builder.setView(view);
		// находим NumberPicker для отображения кол-ва
		InitNumberPickerDefaultLimit(view);
		//вешаем события на кнопки
		// Save / Edit
		final Button OkButton = (Button) view.findViewById(R.id.ok_item_exercise_button);
		OkButton.setOnClickListener(new View.OnClickListener(){
			//обработка кнопки OK диалога создания элемента сессии
			private boolean isEdit=false;
			private String itemId="";
			private LinearLayout DialogView = null;
			AlertDialog dialog_main = null;
			@Override
			public void onClick(View v) {

				Button b = (Button) v;

				if( b.getText().equals( currContext.getResources().getString(R.string.edit_text) ) ){
					isEdit=true;
					DialogView=DialogEditView; dialog_main = dialogEdit;
					itemId = getItemId();
				}
				else if ( b.getText().equals( currContext.getResources().getString(R.string.save_text) ) ) {
					DialogView=DialogAddView; dialog_main = dialogAdd;
					isEdit=false;
				}
				//System.out.println("isEdit="+isEdit);

				long time=0;
				String item_name="";

				//Вычисляем time//
				NumberPicker np;
				//Hours
				np = (NumberPicker) DialogView.findViewById(R.id.NumberPicker01);
				time=60*60*np.getValue()*1000;
				//Minutes
				np = (NumberPicker) DialogView.findViewById(R.id.NumberPicker02);
				time=time+60*np.getValue()*1000;
				//Seconds
				np = (NumberPicker) DialogView.findViewById(R.id.NumberPicker03);
				time=time+np.getValue()*1000;
				//--------------//

				//Фиксируем название
				EditText et;
				et = (EditText) DialogView.findViewById(R.id.item_name);
				item_name = et.getText().toString();

				//Проверяем заполнение
				if(CheckDialogValues(DialogView,time,item_name)) {

					if (currExerciseItemId.length()>0) {
						ExerciseItem.updateItem(String.valueOf(currExerciseId),itemId,time,item_name);
					}
					else {
						ExerciseItem.addItem(String.valueOf(currExerciseId),time,item_name);
					}

					if (currContext.toString().contains("ExerciseEditActivity"))
					{
						//((ExerciseEditActivity) currContext).useMatchListView();
						((ExerciseEditActivity) currContext).useNotifyDataSetChanged();
					}
					else if (currContext.toString().contains("IntervalTimerActivity"))
					{
                        ((IntervalTimerActivity) currContext).useNotifyDataSetChanged();
						//((NavigationDrawerFragment.NavigationDrawerCallbacks) currContext).onNavigationDrawerSomeExercise(currExerciseId);//
					}

					dialog_main.cancel();

				}
				else {
					exLog.i("onClickLisenerAdapterItemOk", "CheckDialogValues(DialogView,time,item_name)==false");
				}
			}
		});
		// Cancel
		final Button CancelButton = (Button) view.findViewById(R.id.cancel_item_exercise_button);
		CancelButton.setOnClickListener(onClickLisenerCancel);
		// EditText
		final EditText editText= (EditText) view.findViewById(R.id.item_name);

		showSoftKeyboardOnView(editText);
		//------------------------//
		//*/

		AlertDialog dialog_main;
		dialog_main = builder.create();

		if (currExerciseItemId.length()>0) {
			InitDialogEditValuesItemOfExercise(view);
			DialogEditView = view;
			dialogEdit = dialog_main;
		}
		else {
			InitDialogDefaultValues(view);
			DialogAddView = view;
			dialogAdd = dialog_main;
		}

		DialogView = view;

		return dialog_main;
	}

	private static void InitNumberPickerDefaultLimit(LinearLayout v) {
		NumberPicker np;
		//Hours
		np = (NumberPicker) v.findViewById(R.id.NumberPicker01);
		np.setMaxValue(24);
		np.setMinValue(0);
		//Minutes
		np = (NumberPicker) v.findViewById(R.id.NumberPicker02);
		np.setMaxValue(60);
		np.setMinValue(0);
		//Seconds
		np = (NumberPicker) v.findViewById(R.id.NumberPicker03);
		np.setMaxValue(60);
		np.setMinValue(0);
	}

	private static void InitDialogDefaultValues(LinearLayout v) {
		NumberPicker np;
		//Hours
		np = (NumberPicker) v.findViewById(R.id.NumberPicker01);
		np.setValue(0);
		//Minutes
		np = (NumberPicker) v.findViewById(R.id.NumberPicker02);
		np.setValue(0);
		//Seconds
		np = (NumberPicker) v.findViewById(R.id.NumberPicker03);
		np.setValue(0);
		//Name
		EditText et;
		et = (EditText) v.findViewById(R.id.item_name);
		et.setText("");
		//Button name
		Button b;
		b = (Button) v.findViewById(R.id.ok_item_exercise_button);
		b.setText(R.string.save_text);
	}

	private static void InitDialogEditValuesItemOfExercise(LinearLayout v) {

		System.out.println("InitDialogEditValues");

		long time=0;
		String itemName="";
		Time t;

		Cursor c = db.getExerciseItemData(getItemId());

		if (c.moveToFirst()) {
			// Проходимся по каждой строке
			time = c.getLong(c.getColumnIndex(db.COLUMN_TIME));
			itemName = c.getString(c.getColumnIndex(db.COLUMN_EXERCISE_ITEM_NAME));
		}

		NumberPicker np;
		//Date d = new Date(time);
		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		t = new Time();
		t.switchTimezone("UTC");
		t.set(time);
		//Hours
		np = (NumberPicker) v.findViewById(R.id.NumberPicker01);
		np.setValue(t.hour);
		//Minutes
		np = (NumberPicker) v.findViewById(R.id.NumberPicker02);
		np.setValue(t.minute);
		//Seconds
		np = (NumberPicker) v.findViewById(R.id.NumberPicker03);
		np.setValue(t.second);
		//Name
		EditText et;
		et = (EditText) v.findViewById(R.id.item_name);
		et.setText(itemName);
		//Button name
		Button b;
		b = (Button) v.findViewById(R.id.ok_item_exercise_button);
		b.setText(R.string.edit_text);
	}

	// удаляем сессии //
	private static Dialog DeleteExercisesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
		builder.setMessage(currContext.getResources().getString(R.string.delete_element) );
		//builder.setTitle(R.string.app_name);
		builder.setIcon(R.drawable.ic_tab_delete);

		// создаем кнопку "Yes" и обработчик события
		builder.setPositiveButton(currContext.getResources().getString(R.string.yes_text),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						DeleteItem();
					}
				});

		// создаем кнопку "No" и обработчик события
		builder.setNegativeButton(currContext.getResources().getString(R.string.no_text),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.setCancelable(false);
		return builder.create();

	}
	private static void DeleteItem() {
		exLog.i("DeleteExercises","1 SelectedItems = " + currSelectedItems.toString());
		Object[] ArrSI = currSelectedItems.toArray();
		exLog.i("DeleteExercises","2");
		for(int i=0;i<ArrSI.length;i++)
		{
			exLog.i("DeleteExercises","i="+i);
			if (currEditionMode.equals(Constants.SECTION_ALL_EXERCISES_MODE)){
				exLog.i("DeleteExercises","Exercise delete, id = "+currExerciseId);
				Exercise.deleteExercise((ArrSI[i]).toString());
				//db.deleteExercise(((Integer)ArrSI[i]).intValue());
			}
			else if (currEditionMode.equals(Constants.SELECT_SOME_EXERCISE_MODE)) {
				exLog.i("DeleteExercises","ExerciseItem delete, id = "+currExerciseId);
				ExerciseItem.deleteItemFromExercise((ArrSI[i]).toString(),currExerciseId);
				//db.deleteExercise((ArrSI[i]).toString(),currExerciseId);
			}
		}
		//exLog.i("DeleteExercises","3");
		currSelectedItems.clear();
		((ExerciseEditActivity) currContext).useNotifyDataSetChanged();
		//((ExerciseEditActivity) currContext).useMatchListView();//
	}

}