package com.taktilidu.sporttimer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.core.ApplicationTimer;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.ExerciseItem;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class ExerciseDragItemAdapter extends DragItemAdapter<ExerciseItem, ExerciseDragItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private String mSelectedExerciseItem;
    private ApplicationTimer mApplicationTimer = null;
    private DragListView mDragListView = null;
    private String mExerciseId;
    private DB mDB;
    private final ExerciseItem epmtyAdapterItem = new ExerciseItem("","","",0,0);

    public ExerciseDragItemAdapter(ArrayList<ExerciseItem> list, int layoutId, int grabHandleId, String selectedExerciseItem, String exerciseId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        mSelectedExerciseItem = selectedExerciseItem;
        mExerciseId = exerciseId;
        setHasStableIds(true);
        setItemList(list);
        mDB = DB.giveDBlink();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ExerciseItem exerciseItem = mItemList.get(position);

        holder.mItemName.setText(exerciseItem.getName());
        holder.mTime.setText(exerciseItem.getSTime());
        holder.mNumber.setText(Integer.valueOf(position + 1).toString());

        if (mSelectedExerciseItem == exerciseItem.getId()) {
            holder.mItemStripe.setBackgroundResource(R.drawable.patch_timer_element_red);
        }
        else {
            holder.mItemStripe.setBackgroundResource(0);
        }
        //--------------------------//

        holder.itemView.setTag(mItemList.get(position));

    }

    public Object getItem(int position) {
        if (mItemList.size() != 0)
            return mItemList.get(position);
        else {
            return epmtyAdapterItem;
        }
    }

    public void setSelectedRow(String selectedExerciseItem) {
        this.mSelectedExerciseItem = selectedExerciseItem;
        notifyDataSetChanged();
    }

    public void setSelectedRow(int selectedExerciseItemPosition) {
        ExerciseItem exerciseItem = mItemList.get(selectedExerciseItemPosition);
        setSelectedRow(exerciseItem.getId());
    }

    public void setApplicationTimer(ApplicationTimer applicationTimer) {
        this.mApplicationTimer = applicationTimer;
    }

    public void setDragListView(DragListView dragListView) {
        this.mDragListView = dragListView;
    }

    public void setExerciseId(String exerciseId) {
        this.mExerciseId = exerciseId;
        setItemList(Exercise.mapOfExercises.get(exerciseId).listOfExerciseItems);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).hashCode();
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mItemName;
        TextView mTime;
        TextView mNumber;
        LinearLayout mItemStripe;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewParent mParent = view.getParent();
                    if (mParent != null) {
                        mParent.requestDisallowInterceptTouchEvent(true);
                    }
                    //Toast.makeText(view.getContext(), "OnClick, position = " + (Integer.valueOf((String) mNumber.getText())-1), Toast.LENGTH_SHORT).show();
                    int position = (Integer.valueOf((String) mNumber.getText())-1);
                    if (mApplicationTimer!=null) mApplicationTimer.selectPosition(position); else setSelectedRow(position);
                    if (mDragListView!=null) {
                        mDragListView.setVerticalScrollbarPosition(position);
                    }
                    notifyDataSetChanged();
                }
            });
            markViewItems(itemView);
        }

        private void markViewItems(View rowView) {

            //--------------------------//
            mItemName = (TextView) rowView.findViewById(R.id.item_name);
            //--------------------------//
            mTime = (TextView) rowView.findViewById(R.id.time);
            //--------------------------//
            mNumber = (TextView) rowView.findViewById(R.id.number);
            //--------------------------//
            mItemStripe = (LinearLayout) rowView.findViewById(R.id.layout_stripe);

        }

    }
}