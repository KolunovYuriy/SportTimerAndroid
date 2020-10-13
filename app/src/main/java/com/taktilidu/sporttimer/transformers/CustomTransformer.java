package com.taktilidu.sporttimer.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.taktilidu.sporttimer.R;
import com.taktilidu.sporttimer.common.Constants;
import com.taktilidu.sporttimer.common.exLog;

public class CustomTransformer implements ViewPager.PageTransformer {

    public CustomTransformer() {

    }

    protected void onTransform(View view, float position) {
        final float height = view.getHeight();
        final float width = view.getWidth();
        float elevation = view.getElevation();
        float k = width/2;
        //ViewPropertyAnimator
        //view.setElevation(elevation*Math.abs(position/1));
        //view.setElevation((float) 0.5);
        //view.setTranslationX(position < 0 ? -  width * position * 0.15f :  0f);
        //view.setPivotX((position > 0 && position < 1) ? k+(width-k)*position :  0f);
        //view.setTranslationX((position > 0 && position < 1) ? k+(width-k)*position :  0f);
        float translationX=0f;
        if(view.findViewById(R.id.exerciseSettings)!=null) {
            //exLog.i("onTransform","\r\n"+"  1 view.getTranslationX = " + view.getTranslationX());
            //exLog.i("onTransform","  position = " + position);
            translationX = - k *(position - Constants.DELTA_WIDTH);
            view.setTranslationX((position > 0 && position < 1) ? translationX : 0f);
            //exLog.i("onTransform","  (position > 0 && position < 1) = " + (position > 0 && position < 1));
            //exLog.i("onTransform","  2 view.getTranslationX = " + view.getTranslationX() + "\r\n ");
        }
        else if (view.findViewById(R.id.exercisesList)!=null) {
            exLog.i("onTransform","\r\n"+"  1 view.getTranslationX = " + view.getTranslationX());
            exLog.i("onTransform","  position = " + position);
            //translationX = position*(1f-2*width);//приколный эффект
            translationX = position*(1f-width);
            view.setTranslationX((position > -1 && position < 0) ? translationX : 0f);
            exLog.i("onTransform","  is = " + (position > -1 && position < -0));
            exLog.i("onTransform","  2 view.getTranslationX = " + view.getTranslationX() + "\r\n ");
        }
        else {
            view.setTranslationX(translationX);
        }
        //view.setTranslationX((position > 0 && position < 1) ? k+(width-k)*(position-1) :  0f);
        //view.setTranslationX((position > -2 && position < -1) ? k+(width-k)*(1-position) :  0f);
    }

    public void transformPage(View view, float position) {
        this.onPreTransform(view, position);
        this.onTransform(view, position);
        this.onPostTransform(view, position);
    }

    protected boolean hideOffscreenPages() {
        return false;
    }

    protected boolean isPagingEnabled() {
        return false;
    }

    protected void onPreTransform(View view, float position) {
        float width = (float)view.getWidth();
        view.setRotationX(0.0F);
        view.setRotationY(0.0F);
        view.setRotation(0.0F);
        view.setScaleX(1.0F);
        view.setScaleY(1.0F);
        view.setPivotX(0.0F);
        view.setPivotY(0.0F);
        view.setTranslationY(0.0F);
        view.setTranslationX(this.isPagingEnabled()?0.0F:-width * position);
        if(this.hideOffscreenPages()) {
            view.setAlpha(position > -1.0F && position < 1.0F?1.0F:0.0F);
        } else {
            view.setAlpha(1.0F);
        }

    }

    protected void onPostTransform(View view, float position) {
    }

}
