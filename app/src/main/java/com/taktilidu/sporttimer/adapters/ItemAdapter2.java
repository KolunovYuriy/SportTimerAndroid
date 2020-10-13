package com.taktilidu.sporttimer.adapters;

import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.core.Exercise;
import com.taktilidu.sporttimer.core.ExerciseItem;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class ItemAdapter2 extends DragItemAdapter<Pair<Long, String>, ItemAdapter2.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private int mSelectedRow = 0;
    private String mExerciseId;

    public ItemAdapter2(ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, String exerciseId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        mExerciseId = exerciseId;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ExerciseItem exerciseItem =  Exercise.mapOfExercises.get(mExerciseId).listOfExerciseItems.get(position);

        String text = mItemList.get(position).second;
        holder.mItemName.setText(exerciseItem.getName());
        //holder.mTime.setText(exerciseItem.getSTime());
        holder.mNumber.setText(Integer.valueOf(position + 1).toString());

        if (mSelectedRow == position) {
            holder.mItemStripe.setBackgroundResource(R.drawable.toast3);
        }
        else {
            holder.mItemStripe.setBackgroundResource(0);
        }
        //--------------------------//

        holder.itemView.setTag(mItemList.get(position));

    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
        //return position;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mItemName;
        TextView mTime;
        TextView mNumber;
        LinearLayout mItemStripe;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
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
            mItemStripe = (LinearLayout) rowView.findViewById(R.id.item_stripe);

        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}