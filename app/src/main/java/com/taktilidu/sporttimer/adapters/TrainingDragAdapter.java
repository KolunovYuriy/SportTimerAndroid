package com.taktilidu.sporttimer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taktilidu.sporttimer.IntervalTimerActivity;
import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.Constants;
import com.taktilidu.sporttimer.core.ApplicationTimer;
import com.taktilidu.sporttimer.core.DB;
import com.taktilidu.sporttimer.core.Training;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;

public class TrainingDragAdapter extends DragItemAdapter<Training, TrainingDragAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private Context mContext;
    private boolean mDragOnLongPress;
    private String mSelectedTrainingItem;
    private ApplicationTimer mApplicationTimer = null;
    private DragListView mDragListView = null;
    private DB mDB;
    private final Training epmtyAdapterItem = new Training("","");

    public TrainingDragAdapter(ArrayList<Training> list, int layoutId, int grabHandleId, Context context, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        mContext = context;
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

        Training training = mItemList.get(position);

        holder.mItemName.setText(training.getName());
        holder.mTotalTime.setText(training.getSumTime());
        holder.mTotalStep.setText(training.getCountOfExercises()+" "+ mContext.getString(R.string.exercise_step));
        holder.mId = training.getId();
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

    public void setSelectedRow(String selectedTraining) {
        this.mSelectedTrainingItem = selectedTraining;
        notifyDataSetChanged();
    }

    public void setSelectedRow(int selectedTrainingItemPosition) {
        Training training = mItemList.get(selectedTrainingItemPosition);
        setSelectedRow(training.getId());
    }

    public void setDragListView(DragListView dragListView) {
        this.mDragListView = dragListView;
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).hashCode();
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mItemName;
        TextView mTotalTime;
        TextView mTotalStep;
        String mId;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewParent mParent = view.getParent();
                    if (mParent != null) {
                        mParent.requestDisallowInterceptTouchEvent(true);
                    }
                    Intent i = new Intent();
                    i.putExtra(Constants.ARG_TRAINING_ID, mId);
                    i.setClass(mContext, IntervalTimerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }
            });
            markViewItems(itemView);
        }

        private void markViewItems(View rowView) {

            //--------------------------//
            mItemName = (TextView) rowView.findViewById(R.id.item_name);
            //--------------------------//
            mTotalTime = (TextView) rowView.findViewById(R.id.total_time);
            //--------------------------//
            mTotalStep = (TextView) rowView.findViewById(R.id.total_step);

        }

    }
}