package com.taktilidu.sporttimer.adapters;

import java.util.ArrayList;

import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.CustomBaseAdapter;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.Training;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;

public class ExerciseAdapter extends CustomBaseAdapter {

	private Context mContext;
	private String mTrainingId="";
	private String mExerciseId="";
	private LayoutInflater mInflater;
	private ArrayList<String> selectedItems;
	private ViewGroup mContainer;
	private static final String[] str = {
			"Раз", "Два", "Три", "Четыре", "Пять", "Шесть"
	};
	private Object[] AdapterItems;
	private DB db;
	private boolean isEdit=false;

	@Override
	public void notifyDataSetChanged() {
		exLog.i("ExerciseAdapter notifyDataSetChanged","1");
		AdapterItems = Exercise.listOfExercises.toArray();
		super.notifyDataSetChanged();
	}

	//конструктор 1
	public ExerciseAdapter(Context context
			, LayoutInflater inflater, ViewGroup container) {
		this.mContainer = container;
		constructorParamsInit(context, inflater);
	}

	//конструктор 2
	public ExerciseAdapter(Context context
			, LayoutInflater inflater, ViewGroup container, String exerciseId) {
		this.mExerciseId = exerciseId;
		this.mContainer = container;
		constructorParamsInit(context, inflater);
	}

	//конструктор 3
	public ExerciseAdapter(Context context
			, LayoutInflater inflater, ViewGroup container, String exerciseId, String trainingId) {
		this.mTrainingId = trainingId;
		this.mExerciseId = exerciseId;
		this.mContainer = container;
		this.mContext = context;
		this.mInflater = inflater;
		//собираем данные из БД//
		db = DB.giveDBlink();
		AdapterItems = Training.mapOfTrainings.get(trainingId).listOfExercises.toArray();
	}

	//конструктор 4
	public ExerciseAdapter(Context context
			, LayoutInflater inflater, ArrayList<String> selectedItems) {
		this.selectedItems = selectedItems;
		constructorParamsInit(context, inflater);
		//режим редактирования//
		this.isEdit=true;
		//---------------------//
	}

	private void constructorParamsInit(Context context, LayoutInflater inflater) {
		this.mContext = context;
		this.mInflater = inflater;
		//собираем данные из БД//
		db = DB.giveDBlink();
		AdapterItems = Exercise.listOfExercises.toArray();
	}

	public DB giveDBlink() {
		return db;
	}

	@Override
	public int getCount() {
		//return db.getCountOfTableExercise();
		return AdapterItems.length;
		//return 30;
	}

	@Override
	public Object getItem(int position) {
		return AdapterItems[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView
			, ViewGroup parent) {
		exLog.i("ExerciseAdapter getView","1 position="+position);
		View rowView=convertView;

		Exercise AdapterItem = (Exercise) AdapterItems[position];

		//if (AdapterItem.getRowView()!=null) {rowView = AdapterItem.getRowView();}

		if (rowView==null) {
			exLog.i("ExerciseAdapter getView","2");
			if (!isEdit)
			{
				rowView = mInflater.inflate(
						R.layout.exercises_list_view, null);
			}
			else
			{
				rowView = mInflater.inflate(
						R.layout.exercises_edit_list_view, null);
			}
		}

		exLog.i("ExerciseAdapter getView","3 position="+position);
		TextView TV;
		EditText ET;
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.item_name);
		TV.setText(AdapterItem.getName());
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.total_step);
		TV.setText(String.valueOf(AdapterItem.getCountExerciseItems())+" "+ mContext.getString(R.string.step));
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.total_time);
		TV.setText(AdapterItem.getSumTime());
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.first_time);
		TV.setText(AdapterItem.getSFirstTime());
		exLog.i("ExerciseAdapter getView","3 boolean= " +String.valueOf(AdapterItem.getSumTime().equals(AdapterItem.getSFirstTime())));
		exLog.i("ExerciseAdapter getView","3 left= " +AdapterItem.getSumTime());
		exLog.i("ExerciseAdapter getView","3 right= " +AdapterItem.getSFirstTime());
		if ( AdapterItem.getSumTime().
				equals(AdapterItem.getSFirstTime()) ) {
			//if (true) {
			exLog.i("ExerciseAdapter getView","3 11111111111");
			TV.setTextColor(mContext.getResources().getColor(R.color.grey_custom_color));
		}
		//--------------------------//
		exLog.i("ExerciseAdapter getView","4");
		if(!isEdit){
			ImageView IV = (ImageView) rowView.findViewById(R.id.icTimerABCDE);
			int source = position%5==0  ? R.drawable.ic_timer_a
					: position%5==1  ? R.drawable.ic_timer_b
					: position%5==2  ? R.drawable.ic_timer_c
					: position%5==3  ? R.drawable.ic_timer_d
					: R.drawable.ic_timer_e;
			IV.setImageResource(source);

			if (this.mExerciseId.equals(AdapterItem.getId())) {
				//rowView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_m));
				rowView.setBackgroundColor(mContext.getResources().getColor(R.color.red_custom_color));
				//--------------------------//
				TV = (TextView) rowView.findViewById(R.id.total_step);
				TV.setTextColor(mContext.getResources().getColor(R.color.white_color));
				//--------------------------//
				TV = (TextView) rowView.findViewById(R.id.first_time);
				TV.setTextColor(mContext.getResources().getColor(R.color.white_color));
			}
			else {
				rowView.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
				//--------------------------//
				TV = (TextView) rowView.findViewById(R.id.total_step);
				TV.setTextColor(mContext.getResources().getColor(R.color.grey_custom_color));
				//--------------------------//
				TV = (TextView) rowView.findViewById(R.id.first_time);
				TV.setTextColor(mContext.getResources().getColor(R.color.red_custom_color));
			}
		}
		else {
			ImageView IVchecked = (ImageView) rowView.findViewById(R.id.icCheckItem);
			exLog.i("ExerciseAdapter getView","selectedItems = " + selectedItems.toString());
			exLog.i("ExerciseAdapter getView","id = " + AdapterItem.getId() + ", name = " + AdapterItem.getName() + ", is check = " +selectedItems.contains(AdapterItem.getId()));
			if (this.selectedItems.contains(AdapterItem.getId())) {
				IVchecked.setImageResource(R.drawable.ic_selection_checked);
			}
			else {IVchecked.setImageResource(R.drawable.ic_selection_unchecked);}
		}
		exLog.i("ExerciseAdapter getView","6");
		exLog.i("TraningExerciseAdapterROW","ROW "+
						db.COLUMN_ID+"="+AdapterItem.getId()+"|"+
						db.COLUMN_EXERCISE_NAME+"="+AdapterItem.getName()+"|"+
						"count_of_item"+"="+AdapterItem.getSumTime()+"|"
		);

		exLog.i("ExerciseAdapter getView","7");
		AdapterItem.setRowView(rowView);

		return rowView;
	}

	public void setSelectedItems(ArrayList<String> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public void setExerciseId(String exerciseId) {
		this.mExerciseId = exerciseId;
	}
}
