package com.dantelab.rippleeffectview.library;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * Created by ivan on 05.08.14.
 */
public class RippleFlowBar extends View {

    private int mGravity;
    private int mState;

    public RippleFlowBar(Context context) {
        super(context);
    }

    public RippleFlowBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleFlowBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void init(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = canvas.getWidth();
        int h = canvas.getHeight();


        int radius = w > h ? h/2 : w/2;
        int position = 0;
        switch (mGravity){
            case Gravity.LEFT:
                position = getPaddingLeft();
                break;
            case Gravity.RIGHT:
                position = w - getPaddingRight();
                break;
        }





    }
}
