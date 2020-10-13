package com.taktilidu.sporttimer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.CustomBaseAdapter;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.Training;

import java.util.ArrayList;

public class TrainingAdapter extends CustomBaseAdapter {

	private Context mContext;
	private String mTrainingId="";
	private LayoutInflater mInflater;
	private ArrayList<String> selectedItems;
	private ViewGroup mContainer;
	private static final String[] str = {
			"Раз", "Два", "Три", "Четыре", "Пять", "Шесть"
	};
	private Object[] AdapterItems;
	private DB db;

	@Override
	public void notifyDataSetChanged() {
		exLog.i("TrainingAdapter notifyDataSetChanged","1");
		super.notifyDataSetChanged();
	}

	//конструктор 1
	public TrainingAdapter(Context context
			, LayoutInflater inflater, ViewGroup container) {
		this.mContainer = container;
		constructorParamsInit(context, inflater, false);
	}

	//конструктор 2
	public TrainingAdapter(Context context
			, LayoutInflater inflater, ViewGroup container, boolean isToday) {
        this.mContainer = container;
		constructorParamsInit(context, inflater, isToday);

	}

	private void constructorParamsInit(Context context, LayoutInflater inflater, boolean isToday) {
		this.mContext = context;
		this.mInflater = inflater;
		db = DB.giveDBlink();
        if (isToday)
            AdapterItems = db.fillArrayListOfTodayTraining().toArray();
        else
            AdapterItems = Training.listOfTrainings.toArray();
	}

	public DB giveDBlink() {
		return db;
	}

	@Override
	public int getCount() {
		return AdapterItems.length;
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
		exLog.i("TrainingAdapter getView","1 position="+position);
		View rowView=convertView;

		Training AdapterItem = (Training) AdapterItems[position];

		if (rowView==null) {
			exLog.i("TrainingAdapter getView","2");
			rowView = mInflater.inflate(
						R.layout.trainings_list_view, null);
		}

		exLog.i("TrainingAdapter getView","3 position="+position);
		TextView TV;
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.item_name);
		TV.setText(AdapterItem.getName());
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.total_step);
		TV.setText(String.valueOf(AdapterItem.getCountOfExercises())+" "+ mContext.getString(R.string.exercise_step));
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.total_time);
		TV.setText(AdapterItem.getSumTime());
		//--------------------------//
		exLog.i("TrainingAdapter getView","6");
		exLog.i("TrainingAdapter","ROW "+
						db.COLUMN_ID+"="+AdapterItem.getId()+"|"+
						db.COLUMN_EXERCISE_NAME+"="+AdapterItem.getName()+"|"+
						"count_of_item"+"="+AdapterItem.getSumTime()+"|"
		);

		exLog.i("TrainingAdapter getView","7");
		AdapterItem.setRowView(rowView);

		return rowView;
	}

	public void setSelectedItems(ArrayList<String> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public void setTrainingId(String trainingId) {
		this.mTrainingId = trainingId;
	}
}
