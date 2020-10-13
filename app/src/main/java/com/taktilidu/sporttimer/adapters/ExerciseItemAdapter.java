package com.taktilidu.sporttimer.adapters;

import java.util.ArrayList;

import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.CustomBaseAdapter;
import com.taktilidu.sporttimer.common.exLog;
import com.taktilidu.sporttimer.core.ExerciseItem;
import com.taktilidu.sporttimer.core.Exercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExerciseItemAdapter extends CustomBaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String> selectedItems;
	private ViewGroup container;
	private String exerciseId;
	private int selectedRow = 0;
	//private int regim;//1 - для отображения информации, 2 для модификации информации
	private static final String[] str = {
			"Раз", "Два", "Три", "Четыре", "Пять", "Шесть"
	};
	private Object[] AdapterItems;
	private DB db;
	private boolean isEdit=false;

	ExerciseItem epmtyAdapterItem = new ExerciseItem("","","",0,0);

	@Override
	public void notifyDataSetChanged() {
		exLog.i("ExerciseItemAdapter notifyDataSetChanged","1");
		AdapterItems = getArrayList().toArray();
		super.notifyDataSetChanged();
	}

	//конструктор
	public ExerciseItemAdapter(Context context
			, LayoutInflater inflater, ViewGroup container, String exerciseId) {
		this.context = context;
		this.inflater = inflater;
		this.container = container;
		this.exerciseId = exerciseId;
		exLog.i("MyLogs","ExerciseItemAdapter EXERCISE_ID="+exerciseId);
		//собираем данные из БД//
		db = DB.giveDBlink();
		AdapterItems = getArrayList().toArray();
		//---------------------//
	}

	public ExerciseItemAdapter(Context context
			, LayoutInflater inflater, ViewGroup container, String exerciseId, int selectedRow) {
		this.selectedRow = selectedRow;
		new ExerciseItemAdapter(context,inflater,container,exerciseId);
	}

	public ExerciseItemAdapter(Context context
			, LayoutInflater inflater, String exerciseId, ArrayList<String> selectedItems) {
		this.context = context;
		this.inflater = inflater;
		this.exerciseId = exerciseId;
		this.selectedItems = selectedItems;
		exLog.i("MyLogs","ExerciseItemAdapter EXERCISE_ID="+exerciseId);
		//собираем данные из БД//
		db = DB.giveDBlink();
		AdapterItems = getArrayList().toArray();
		//режим редактирования//
		isEdit=true;
		//---------------------//
	}

	public ArrayList<ExerciseItem> getArrayList() {
		return Exercise.mapOfExercises.get(this.exerciseId).listOfExerciseItems;
	}

	public DB giveDBlink() {
		return db;
	}

	@Override
	public int getCount() {
		return db.getCountOfExerciseItems(exerciseId);
	}

	@Override
	public Object getItem(int position) {
		exLog.i("AdapterItems.length = " + AdapterItems.length);
		if (AdapterItems.length != 0)
			return AdapterItems[position];
		else {
			return epmtyAdapterItem;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView
			, ViewGroup parent) {
		exLog.i("ExerciseItemAdapter getView","1");
		View rowView=convertView;

		if (rowView==null) {
			exLog.i("ExerciseItemAdapter getView","2");
			if (!isEdit)
			{
				rowView = inflater.inflate(
						R.layout.exercise_items_list_view, null);
			}
			else
			{
				rowView = inflater.inflate(
						R.layout.exercise_items_edit_list_view, null);
			}
		}

		exLog.i("ExerciseItemAdapter getView","3 position="+position);

		ExerciseItem AdapterItem = (ExerciseItem) AdapterItems[position];

		TextView TV;
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.item_name);
		TV.setText( AdapterItem.getName());
		//--------------------------//
		TV = (TextView) rowView.findViewById(R.id.time);
		TV.setText(AdapterItem.getSTime());
		//--------------------------//
		if (!isEdit)
		{
			TV = (TextView) rowView.findViewById(R.id.number);
			TV.setText(Integer.valueOf(position+1).toString());

			//ImageView IV = (ImageView) rowView.findViewById(R.id.icPosition);
			LinearLayout LL = (LinearLayout) rowView.findViewById(R.id.item_stripe);
			if (selectedRow == position) {
				LL.setBackgroundResource(R.drawable.toast3);
			}
			else {
				LL.setBackgroundResource(0);
			}
		}
		else {
			ImageView IVchecked = (ImageView) rowView.findViewById(R.id.icCheckItem);
			if (this.selectedItems.contains(AdapterItem.getId())) {
				IVchecked.setImageResource(R.drawable.ic_selection_checked);
			}
			else {IVchecked.setImageResource(R.drawable.ic_selection_unchecked);}
		}
		//--------------------------//		

		exLog.i("ExerciseItemAdapter getView","7	");
		AdapterItem.setRowView(rowView);
		return rowView;
	}

	@Override
	public void setSelectedItems(ArrayList<String> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public void setExerciseId(String exerciseId) {
		this.exerciseId = exerciseId;
	}

	public void exchangePosition(int onDragPosition, int destnation_position) {
		ExerciseItem onDrag_data = getArrayList().get(onDragPosition);
		ExerciseItem finger_data = getArrayList().get(destnation_position);
		getArrayList().remove(onDragPosition);
		getArrayList().add(onDragPosition, finger_data);
		getArrayList().remove(destnation_position);
		getArrayList().add(destnation_position, onDrag_data);
		notifyDataSetChanged();
	}

}
