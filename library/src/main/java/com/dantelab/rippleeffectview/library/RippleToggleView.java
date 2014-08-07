package com.dantelab.rippleeffectview.library;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by ivan on 04.08.14.
 */
public class RippleToggleView extends CheckBox implements View.OnTouchListener {
    RippleOval ripple;
    int mRippleColor;
    int mStartAlpha;
    Paint mPaint;
    private float _x;
    private float _y;
    private int mAnimationDuration;

    public RippleToggleView(Context context) {
        super(context);
        init(context);
    }

    public RippleToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context){
        mRippleColor = Color.argb(255, 127, 127, 127);
        mStartAlpha = 55;
        mPaint = new Paint();
        mPaint.setColor(mRippleColor);
        mPaint.setStyle(Paint.Style.FILL);
        setOnTouchListener(this);
    }

    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RippleEffectView,
                0, 0);
        try {
            mRippleColor = a.getColor(R.styleable.RippleEffectView_rippleColor, Color.argb(255, 127,127,127));
            mStartAlpha = a.getInt(R.styleable.RippleEffectView_rippleStartAlpha, 55);
            mAnimationDuration = a.getInt(R.styleable.RippleEffectView_rippleAnimationDuration, 150);
        } finally {
            a.recycle();
        }
    }


    public int getRippleColor() {
        return mRippleColor;
    }

    public void setRippleColor(int mRippleColor) {
        this.mRippleColor = mRippleColor;
    }

    public int getStartAlpha() {
        return mStartAlpha;
    }

    public void setStartAlpha(int mStartAlpha) {
        this.mStartAlpha = mStartAlpha;
    }

    @Override
    public void toggle() {
        super.toggle();

        if (ripple!=null)ripple.end();

            float w = getWidth();
            float h = getHeight();

            w = w-_x > _x ? w - _x : _x;
            h = h - _y > _y ? h - _y : _y;
            float radius = (float) Math.sqrt(w*w+h*h);
             final RippleOval oval;
        if (isChecked()) {
                oval = new RippleOval((int) _x, (int) _y, getResources().getDimensionPixelSize(R.dimen.startRadius), radius, mStartAlpha, mAnimationDuration);

            }  else {
            oval = new RippleOval((int) _x, (int) _y, radius,  getResources().getDimensionPixelSize(R.dimen.startRadius),mStartAlpha, mAnimationDuration);
             }

             oval.setFadeOutAfter(false);
                 oval.setAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ripple = null;
                    invalidate();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    ripple = null;
                    invalidate();
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        oval.addUpdateListener(new RippleOval.Listener() {
            @Override
            public void update() {
                invalidate();
            }
        });
            ripple = oval;
            ripple.start();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (ripple != null) {
            Point point = ripple.getPoint();
            mPaint.setColor(mRippleColor);
            mPaint.setAlpha(ripple.getAlpha());
            canvas.drawCircle(point.x, point.y, ripple.getRadius(), mPaint);
        } else
            if (isChecked()){
                mPaint.setColor(mRippleColor);
                mPaint.setAlpha(mStartAlpha);
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
            }
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        _x = motionEvent.getX();
        _y = motionEvent.getY();
        return false;
    }

}
