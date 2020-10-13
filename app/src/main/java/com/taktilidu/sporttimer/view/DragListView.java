package com.taktilidu.sporttimer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.taktilidu.sporttimer.adapters.ExerciseItemAdapter;

public class DragListView extends ListView {

    private Boolean isOnDrag = false;
    private int StatusHeight;
    private int destination_position;
    private int last_destination_position;
    private View destination_view;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private ImageView dragImgView = null;

    private int downx = 0;
    private int downy = 0;

    private int img_show = 0;
    private int img_remove = 1;

    private int actionbarHeight = 0;
    private View last_destination_position_view;

    private SparseArray view_map;

    private SparseArray smoothing_map;

    private int smooth_last_position = -1;

    public DragListView(Context context) {
        super(context);
        initViews(context);
    }

    public DragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context mContext) {
        this.setOnItemLongClickListener(listener);
        StatusHeight = getStatusHeight(mContext);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams();

        dragImgView = new ImageView(mContext);
        dragImgView.setTag(img_show);
        view_map = new SparseArray();
        smoothing_map = new SparseArray();
    }

    public void setActionBarHeight(int height) {
        this.actionbarHeight = height;
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downx = (int) ev.getRawX();
                downy = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnDrag) {
                    params.x = (int) ev.getRawX() - (dragImgView.getWidth() / 2);
                    params.y = (int) ev.getRawY() - (dragImgView.getHeight() / 2);
                    mWindowManager.updateViewLayout(dragImgView, params);
                    if (destination_view != null) destination_view.setVisibility(VISIBLE);

                    int real_heihth = (int) ev.getRawY() - actionbarHeight - StatusHeight;
                    destination_position = pointToPosition((int) ev.getRawX(), real_heihth);

                    destination_view = getChildAt(destination_position - getFirstVisiblePosition());
                    smoothScroll(destination_position);
                    last_destination_position_view = getChildAt(last_destination_position - getFirstVisiblePosition());
                    if (destination_position != AdapterView.INVALID_POSITION) {
                        ExerciseItemAdapter adapter = (ExerciseItemAdapter) getAdapter();
                        if (destination_view != null) {
                            destination_view.setVisibility(INVISIBLE);
                            view_map.put(destination_position, destination_view);
                        }
                        if (destination_position != last_destination_position) {
                            if (last_destination_position_view == null) {
                                last_destination_position_view = (View) view_map.get(last_destination_position);
                            }
                            last_destination_position_view.setVisibility(VISIBLE);
                            adapter.exchangePosition(last_destination_position, destination_position);
                            last_destination_position = destination_position;
                        }
                    } else {
                        if (last_destination_position_view == null) {
                            last_destination_position_view = (View) view_map.get(last_destination_position);
                        }
                        last_destination_position_view.setVisibility(INVISIBLE);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int tag = (int) dragImgView.getTag();
                if (isOnDrag && tag == img_show) {
                    dragImgView.setTag(img_remove);
                    mWindowManager.removeView(dragImgView);
                    if (last_destination_position_view != null)
                        last_destination_position_view.setVisibility(VISIBLE);
                    if (destination_view != null) destination_view.setVisibility(VISIBLE);

                }

                isOnDrag = false;
                break;
            default:
                break;

        }
        return super.onTouchEvent(ev);
    }

    public void smoothScroll(final int current_position) {
        int all_show_count = getChildCount();
        int frist_postion = getFirstVisiblePosition();
        int up_boundary_postion = frist_postion + 1;
        int down_boundary_postion = frist_postion + all_show_count - 1;
        Boolean isSmooth = (Boolean) smoothing_map.get(current_position);
        if (isSmooth != null && isSmooth == true) return;
        if (smooth_last_position == -1)
            smooth_last_position = current_position;
        int distance = Math.abs(current_position - smooth_last_position);
        if (distance >= all_show_count) return;
        Runnable runnable = null;
        if (current_position <= up_boundary_postion) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    smoothing_map.put(current_position, true);
                    smoothScrollToPosition(current_position - 1);
                    smoothing_map.put(current_position, false);
                }
            };
        } else if (current_position >= down_boundary_postion) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    smoothing_map.put(current_position, true);
                    smoothScrollToPosition(current_position + 1);
                    smoothing_map.put(current_position, false);
                }
            };
        }
        smooth_last_position = current_position;
    }

    public OnItemLongClickListener listener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            {
                isOnDrag = true;
                last_destination_position = position;

                dragImgView.setTag(img_show);

                last_destination_position_view = view;
                view.setVisibility(INVISIBLE);
                view.destroyDrawingCache();
                view.setDrawingCacheEnabled(true);

                Bitmap dragBitmap = Bitmap.createBitmap(view.getDrawingCache());
                dragImgView.setImageBitmap(dragBitmap);
                params.gravity = Gravity.TOP | Gravity.LEFT;
                params.height = (int) (1.2f * view.getHeight());
                params.width = (int) (1.2f * view.getWidth());
                params.x = downx - (params.width / 2);
                params.y = downy - (params.height / 2);
                params.format = PixelFormat.TRANSLUCENT;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

                mWindowManager.addView(dragImgView, params);
                return true;
            }
        }
    };
}