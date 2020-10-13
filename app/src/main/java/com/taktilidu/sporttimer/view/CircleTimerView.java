package com.taktilidu.sporttimer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.exLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CircleTimerView extends View {

    private String TAG;

    // Default time value
    private long oneHour = 60*60*1000;
    private long oneMinute = 60*1000;

    // Status
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_RADIAN = "status_radian";

    // Default time format
    public static final String LONG_TIME_FORMAT = "HH:mm:ss.SSS";
    public static final float LARGE_TIMER_NUMBER_SIZE = 35;
    public static final String SHORT_TIME_FORMAT = "HH:mm:ss";
    public static final float SMALL_TIMER_NUMBER_SIZE = 23;
    private static final String DEFAULT_TIME_FORMAT = LONG_TIME_FORMAT;

    // Default dimension in dp/pt
    private static final float DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 38;
    private static final float DEFAULT_NUMBER_SIZE = 10;
    private static final float DEFAULT_LINE_WIDTH = 0.5f;
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 15;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 1;
    private static final float DEFAULT_TIMER_NUMBER_SIZE = SMALL_TIMER_NUMBER_SIZE;
    private static final float DEFAULT_TIMER_TEXT_SIZE = 25;

    // Default color
    private static final int DEFAULT_CIRCLE_COLOR = 0xFFE9E2D9;
    //private final int DEFAULT_CIRCLE_BUTTON_COLOR = getResources().getColor(R.color.red_custom);
    private final int DEFAULT_CIRCLE_BUTTON_COLOR = getResources().getColor(R.color.white_color);
    private final int DEFAULT_LINE_COLOR = getResources().getColor(R.color.red_custom_color);
    private static final int DEFAULT_HIGHLIGHT_LINE_COLOR = 0xFF68C5D7;
    private final int DEFAULT_NUMBER_COLOR = getResources().getColor(R.color.red_custom_poor_color);
    private final int DEFAULT_TIMER_NUMBER_COLOR = getResources().getColor(R.color.white_color);
    private static final int DEFAULT_TIMER_COLON_COLOR = 0xFFFA7777;
    private final int DEFAULT_TIMER_START_TEXT_COLOR = getResources().getColor(R.color.red_custom_color);
    private final int DEFAULT_TIMER_STOP_TEXT_COLOR = getResources().getColor(R.color.white_color);//0x99F0F9FF;

    // Paint
    private Paint mHighlightLinePaint;
    private Paint mLinePaint;
    private Paint mCircleButtonPaint;
    private Paint mCirclePaintStart;
    private Paint mCirclePaintStop;
    private Paint mTimerNumberPaint;
    private Paint mTimerTextPaintStart;
    private Paint mTimerTextPaintStop;
    private Paint mTimerColonPaint;

    // Dimension
    private float mGapBetweenCircleAndLine;
    private float mNumberSize;
    private float mLineWidth;
    private float mCircleButtonRadius;
    private float mCircleStrokeWidth;
    private float mTimerNumberSize;
    private float mTimerTextSize;

    // Color
    private int mCircleButtonColor;
    private int mLineColor;
    private int mHighlightLineColor;
    private int mCircleColorStart;
    private int mCircleColorStop;
    private int mTimerNumberColor;
    private int mTimerTextColorStart;
    private int mTimerTextColorStop;

    // Parameters
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mCurrentRadian;
    private float mPreRadian;
    private boolean mInCircleButton;
    private boolean mIsStart=false;
    private long mCurrentTime;
    private String mTimeFormat = DEFAULT_TIME_FORMAT;

    private OnTimeChangedListener mOnTimeChangedListener;
    private OnClickCenterCircleListener mOnClickCenterCircleListener;
    private String mCentralText="";
    private ViewParent mParent;

    public CircleTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public CircleTimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTimerView(Context context) {
        this(context, null);
    }

    private void initialize() {
        exLog.i(TAG,"initialize");
        // Set default dimension or read xml attributes
        mGapBetweenCircleAndLine = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE,
                getContext().getResources().getDisplayMetrics());
        mNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_NUMBER_SIZE, getContext().getResources()
                .getDisplayMetrics());
        mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, getContext().getResources()
                .getDisplayMetrics());
        mCircleButtonRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BUTTON_RADIUS, getContext()
                .getResources().getDisplayMetrics());
        mCircleStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_STROKE_WIDTH, getContext()
                .getResources().getDisplayMetrics());
        mTimerNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_NUMBER_SIZE, getContext()
                .getResources().getDisplayMetrics());//TODO
        mTimerTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIMER_TEXT_SIZE, getContext()
                .getResources().getDisplayMetrics());

        // Set default color or read xml attributes
        mCircleColorStart = DEFAULT_CIRCLE_COLOR;
        mCircleButtonColor = DEFAULT_CIRCLE_BUTTON_COLOR;
        mLineColor = DEFAULT_LINE_COLOR;
        mHighlightLineColor = DEFAULT_HIGHLIGHT_LINE_COLOR;
        mCircleColorStart = DEFAULT_NUMBER_COLOR;
        mCircleColorStop = DEFAULT_NUMBER_COLOR;
        mTimerNumberColor = DEFAULT_TIMER_NUMBER_COLOR;
        mTimerTextColorStart = DEFAULT_TIMER_START_TEXT_COLOR;
        mTimerTextColorStop = DEFAULT_TIMER_STOP_TEXT_COLOR;

        // Init all paints
        mCircleButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintStop = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintStart = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerTextPaintStart = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerTextPaintStop = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimerColonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // CircleButtonPaint
        mCircleButtonPaint.setColor(mCircleButtonColor);
        mCircleButtonPaint.setAntiAlias(true);
        mCircleButtonPaint.setStyle(Paint.Style.FILL);

        // LinePaint
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mCircleButtonRadius * 2 - 80);
        mLinePaint.setStyle(Paint.Style.STROKE);

        // HighlightLinePaint
        mHighlightLinePaint.setColor(mHighlightLineColor);
        mHighlightLinePaint.setStrokeWidth(mLineWidth);

        // CirclePaintStart
        mCirclePaintStop.setColor(mCircleColorStop);
        mCirclePaintStop.setStyle(Paint.Style.FILL);
        mCirclePaintStop.setStrokeWidth(mCircleButtonRadius * 2 - 80);

        // CirclePaint
        mCirclePaintStart.setColor(mCircleColorStart);
        mCirclePaintStart.setStyle(Paint.Style.STROKE);
        mCirclePaintStart.setStrokeWidth(mCircleButtonRadius * 2 - 80);

        // TimerNumberPaint
        mTimerNumberPaint.setColor(mTimerNumberColor);
        mTimerNumberPaint.setTextSize(mTimerNumberSize);
        mTimerNumberPaint.setTextAlign(Paint.Align.CENTER);

        // TimerTextPaintStart
        mTimerTextPaintStart.setColor(mTimerTextColorStart);
        mTimerTextPaintStart.setTextSize(mTimerTextSize);
        mTimerTextPaintStart.setTextAlign(Paint.Align.CENTER);

        // TimerTextPaintStop
        mTimerTextPaintStop.setColor(mTimerTextColorStop);
        mTimerTextPaintStop.setTextSize(mTimerTextSize);
        mTimerTextPaintStop.setTextAlign(Paint.Align.CENTER);

        // TimerColonPaint
        mTimerColonPaint.setColor(DEFAULT_TIMER_COLON_COLOR);
        mTimerColonPaint.setTextAlign(Paint.Align.CENTER);
        mTimerColonPaint.setTextSize(mTimerNumberSize);

        // Solve the target version related to shadow
        // setLayerType(View.LAYER_TYPE_SOFTWARE, null); // use this, when targetSdkVersion is greater than or equal to api 14
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();

        canvas.drawCircle(mCx, mCy, mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine, mIsStart ? mCirclePaintStart : mCirclePaintStop);
        canvas.save();

        canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
        canvas.drawCircle(mCx, getMeasuredHeight() / 2 - mRadius + mCircleStrokeWidth / 2 + mGapBetweenCircleAndLine, mCircleButtonRadius, mCircleButtonPaint);//
        canvas.restore();
        canvas.save();

        canvas.drawText(getSTime(), mCx, mCy + getFontHeight(mTimerNumberPaint) / 2, mTimerNumberPaint);
        if (!mCentralText.equals("")) {
            canvas.drawText(mCentralText, mCx, mCy + getFontHeight(mTimerNumberPaint) / 2 + mTimerNumberSize, mIsStart ? mTimerTextPaintStart : mTimerTextPaintStop);
            //mCentralText="";
        }

        if (null != mOnTimeChangedListener) {
            //mOnTimeChangedListener.onChange(this);
            /*
            if (ismInCircleButton) {
                mOnTimeChangedListener.start(getSTime());
            } else {
                mOnTimeChangedListener.end(getSTime());
            }
            //*/
        }

        canvas.restore();
        // Timer Text
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }

    private float getFontHeight(Paint paint) {
        // FontMetrics sF = paint.getFontMetrics();
        // return sF.descent - sF.ascent;
        Rect rect = new Rect();
        paint.getTextBounds("1", 0, 1, rect);
        return rect.height();
    }

    private void attemptClaimDrag() {
        mParent = getParent();
        if (mParent != null) {
            mParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        exLog.i("CircleTimerView onTouchEvent","event.getAction() & event.getActionMasked() = " + (event.getAction() & event.getActionMasked()));
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // If the point in the circle button
                attemptClaimDrag();
                exLog.i(TAG,"MotionEvent.ACTION_DOWN");
                if (mInCircleButton(event.getX(), event.getY()) && isEnabled()) {

                    //attemptClaimDrag();

                    exLog.i(TAG,"MotionEvent.ACTION_DOWN, mInCircleButton");
                    mInCircleButton = true;
                    mPreRadian = getRadian(event.getX(), event.getY());
                    Log.d(TAG, "In circle button");
                }
                else if ((inMainCircle(event.getX(), event.getY())) && isEnabled()) {
                    exLog.i(TAG,"MotionEvent.ACTION_DOWN, inMainCircle");
                    if (null != mOnClickCenterCircleListener) {
                        mOnClickCenterCircleListener.onClick(this);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                exLog.i("CircleTimerView ACTION_MOVE","mInCircleButton = " + mInCircleButton + "\r\n" + ", event.X = " + event.getX() + "\r\n" + ", event.Y = " + event.getY());
                if (mInCircleButton && isEnabled()) {
                    float temp = getRadian(event.getX(), event.getY());
                    if (mPreRadian > Math.toRadians(270) && temp < Math.toRadians(90)) {
                        mPreRadian -= 2 * Math.PI;
                    } else if (mPreRadian < Math.toRadians(90) && temp > Math.toRadians(270)) {
                        mPreRadian = (float) (temp + (temp - 2 * Math.PI) - mPreRadian);
                    }
                    mCurrentRadian += (temp - mPreRadian);
                    mPreRadian = temp;
                    if (mCurrentRadian > 2 * Math.PI) {
                        mCurrentRadian -= (float) (2 * Math.PI);
                    }
                    if (mCurrentRadian < 0) {
                        mCurrentRadian += (float) (2 * Math.PI);
                    }

                    // set CurrentTime
                    long nextTime = (long) (60 / (2 * Math.PI) * mCurrentRadian * 1000 + (mCurrentTime/oneMinute) * oneMinute);
                    //exLog.i("(nextTime - mCurrentTime)="+(nextTime - mCurrentTime));
                    if (Math.abs(nextTime - mCurrentTime)>((float)(60*1000)/2)) {
                        if ((nextTime - mCurrentTime) < 0) {
                            nextTime += oneMinute;
                        } else {
                            nextTime -= oneMinute;
                        }
                    }
                    //exLog.i("nextTime="+nextTime);
                    if (nextTime>=0){
                        mCurrentTime = nextTime;//(long) (60 / (2 * Math.PI) * mCurrentRadian * 1000);//TODO
                    }
                    else {
                        mCurrentTime = 0;
                        mCurrentRadian = 0;
                    }

                    if (null != mOnTimeChangedListener) {
                        mOnTimeChangedListener.onChange(this);
                    }

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                exLog.i(TAG,"MotionEvent.ACTION_UP");
                if (mInCircleButton && isEnabled()) {
                    exLog.i(TAG,"MotionEvent.ACTION_UP, mInCircleButton");
                    if (null != mOnTimeChangedListener) {mOnTimeChangedListener.onActionUp(this);}
                    mInCircleButton = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                exLog.i(TAG,"MotionEvent.ACTION_CANCEL");
                exLog.i("CircleTimerView ACTION_CANCEL","mInCircleButton = " + mInCircleButton + "\r\n" + ", event.X = " + event.getX() + "\r\n" + ", event.Y = " + event.getY());
                break;
        }
        return true;
    }

    // Whether the down event inside circle button
    private boolean mInCircleButton(float x, float y) {
        float r = mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine;
        float x2 = (float) (mCx + r * Math.sin(mCurrentRadian));
        float y2 = (float) (mCy - r * Math.cos(mCurrentRadian));
        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) < mCircleButtonRadius) {
            return true;
        }
        return false;
    }

    // Whether the down event inside main circle
    private boolean inMainCircle(float x, float y) {
        float r = mRadius - mCircleStrokeWidth / 2 - mGapBetweenCircleAndLine;
        float x2 = (float) (mCx);
        float y2 = (float) (mCy);
        if (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) < r) {
            return true;
        }
        return false;
    }

    // Use tri to cal radian
    private float getRadian(float x, float y) {
        float alpha = (float) Math.atan((x - mCx) / (mCy - y));
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI;
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI;
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (float) (2 * Math.PI + alpha);
        }
        return alpha;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        exLog.d(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Ensure width = height
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.mCx = width / 2;
        this.mCy = height / 2;
        // Radius
        if (mGapBetweenCircleAndLine + mCircleStrokeWidth >= mCircleButtonRadius) {
            this.mRadius = width / 2 - mCircleStrokeWidth / 2;
        } else {
            this.mRadius = width / 2 - (mCircleButtonRadius - mGapBetweenCircleAndLine -
                    mCircleStrokeWidth / 2);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            mCurrentRadian = bundle.getFloat(STATUS_RADIAN);
            mCurrentTime = (long) (60 / (2 * Math.PI) * mCurrentRadian * 60*1000);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setOnTimeChangedListener(OnTimeChangedListener listener) {
        if (null != listener) {
            this.mOnTimeChangedListener = listener;
        }
    }

    public interface OnTimeChangedListener {
        void onChange(View v);
        void onActionUp(View v);
    }

    public void setOnClickCenterCircleListener(OnClickCenterCircleListener listener) {
        if (null != listener) {
            this.mOnClickCenterCircleListener = listener;
        }
    }

    public interface OnClickCenterCircleListener {
        void onClick(View v);
    }

    public void setTime(long time) {
        mCurrentTime = time;
        //exLog.i("second="+(mTime%oneMinute/1000));
        switch (mTimeFormat) {
            case SHORT_TIME_FORMAT:
                mCurrentRadian = (float) ((2*Math.PI)*((float)(mCurrentTime%oneMinute/1000)/60));//millisecond
                break;
            case LONG_TIME_FORMAT:
            default:
                mCurrentRadian = (float) ((2*Math.PI)*((float)(mCurrentTime%oneMinute)/1000/60));//second
                break;
        }
        invalidate();
    }

    public void setTimeFormat (String format) {
        mTimeFormat = format;
        switch (mTimeFormat) {
            case SHORT_TIME_FORMAT:
                setTimerNumberSize(LARGE_TIMER_NUMBER_SIZE);//second
                break;
            case LONG_TIME_FORMAT:
            default:
                setTimerNumberSize(SMALL_TIMER_NUMBER_SIZE);//millisecond
                break;
        }
    }

    public void setDefaultTimerNumberSize() {
        setTimerNumberSize(DEFAULT_TIMER_NUMBER_SIZE);
    }

    public void setTimerNumberSize(float timerNumberSize) {
        mTimerNumberSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, timerNumberSize, getContext()
                .getResources().getDisplayMetrics());
        invalidate();
    }

    public void setCentralText(String text) {
        mCentralText = text;
        invalidate();
    }

    public void setTimerNumberColor(int color) {
        mTimerNumberColor = color;
        mTimerNumberPaint.setColor(mTimerNumberColor);
        invalidate();
    }

    public void setIsStart(boolean isStart) {
        mIsStart = isStart;
    }

    public boolean getIsStart() {
        return mIsStart;
    }

    public long getTime() {
        return mCurrentTime;
    }

    public String getSTime() {
        return getSTime(mTimeFormat);
    }

    public String getSTime(String format) {
        return timeFormat(mCurrentTime,format);
    }

    //Time Format for application//
    private static String timeFormat(long t, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        try {sdf.setTimeZone(TimeZone.getTimeZone("UTC"));} catch (Exception e) {}
        return sdf.format(new Date(t));
    }

    public static String timeFormatFull(long t) {
        String format = "HH:mm:ss.SSS";
        return timeFormat(t, format);
    }

    public static String timeFormatShort(long t) {
        String format = "HH:mm:ss";
        return timeFormat(t, format);
    }
    //---------------------------//

}
