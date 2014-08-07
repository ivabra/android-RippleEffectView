package com.dantelab.rippleeffectview.library;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;

/**
 * Created by ivan on 04.08.14.
 */
public class RippleEffectView extends View {


    public enum RIPPLE_TYPE {
        SOFT, STRONG
    }

    public static final int ANIMATION_DURATION_DEFAULT = 250;
    public static final int ANIMATION_DURATION_MIN = 150;
    public static final int ANIMATION_DURATION_MAX = 1000;



    public static final int START_ALPHA_DEFAULT = 55;
    public static int RIPPLE_COLOR_DEFAULT = Color.argb(255, 127, 127, 127);
    private RIPPLE_TYPE mRippleEdgeEffectType;
    private ArrayList<RippleOval> mOvalShapes;
    private RippleOval mCurrentShape;
    private int mRippleColor;
    private int mStartAlpha;
    private int mAnimationDuration;
    private long mClickDelay;
    private boolean mReverse;
    private boolean mRippleEnable;

    private Rect mDrawRect;

    //   private int[] mColors = new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK};
    //  private float[] position = new float[]{0, 0.5f, 1f};
    //private RadialGradient mShader;

    private Paint mPaint;

    private OnClickListener mClickListener;
    private OnTouchListener mTouchListener;
    private OnLongClickListener mLongClickListener;




    public void setReverse(boolean reverse) {
        this.mReverse = reverse;
        postInvalidate();
    }

    public boolean isRippleEnable() {
        return mRippleEnable;
    }

    public void setRippleEnable(boolean enable) {
        this.mRippleEnable = enable;
    }



    public void setEdgeEffectType(RIPPLE_TYPE type) {
        this.mRippleEdgeEffectType = type;
        postInvalidate();
    }

    public RIPPLE_TYPE getRippleEdgeEffectType(){
        return mRippleEdgeEffectType;
    }

    public void setAnimationDuration(int duration) {
        if (duration > ANIMATION_DURATION_MAX) duration = ANIMATION_DURATION_MAX;
        else  if (duration < ANIMATION_DURATION_MIN) duration = ANIMATION_DURATION_MIN;
        this.mAnimationDuration = duration;
        postInvalidate();
    }

    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setStartAlpha(int startAlpha) {
        this.mStartAlpha = startAlpha;
        postInvalidate();
    }

    public int getStartAlpha() {
        return mStartAlpha;
    }

    public void setClickDelay(int clickdelay) {
        this.mClickDelay = clickdelay;
        postInvalidate();
    }

    public long getClickDelay() {
        return mClickDelay;
    }

    public void setRippleColor(int rippleColor) {
        this.mRippleColor = rippleColor;
        postInvalidate();
    }

    public int getRippleColor() {
        return mRippleColor;
    }





    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.mTouchListener = l;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        this.mLongClickListener = l;
    }

    private OnClickListener mInternalClickListener = new OnClickListener() {

        private boolean mClicked = false;

        @Override
        public void onClick(final View v) {
            if (mClicked) return;
            mClicked = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mClickListener != null)
                        mClickListener.onClick(v);
                    mClicked = false;
                }
            }, mClickDelay);
        }
    };

    private OnTouchListener mInternalTouchListener = new OnTouchListener() {



        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (mTouchListener != null) mTouchListener.onTouch(view, motionEvent);

            if (!isRippleEnable()) return false;

            float _x = motionEvent.getX();
            float _y = motionEvent.getY();


            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    float w = getWidth();
                    float h = getHeight();

                    w = w - _x > _x ? w - _x : _x;
                    h = h - _y > _y ? h - _y : _y;
                    float radius = (float) Math.sqrt(w * w + h * h);
                    final RippleOval sh = new RippleOval((int) _x, (int) _y, getResources().getDimensionPixelOffset(R.dimen.startRadius), radius, mStartAlpha, mAnimationDuration);
                    sh.setFadeOutAfter(false);

                    sh.setAnimatorListener(new ShapeAnimatorListener(sh){

                        @Override
                        public void endAnimation(Animator animator){
                            if (mCurrentShape!=null) {
                                if (getShape() == mCurrentShape) {
                                    if (mLongClickListener != null) {
                                        Point point = getShape().getPoint();
                                        if (mDrawRect.contains(point.x, point.y)) {
                                            mCurrentShape = null;
                                            mLongClickListener.onLongClick(RippleEffectView.this);
                                        }
                                        getShape().startAlphaAnimation(new ShapeAnimatorRemoveListener(getShape()));

                                    }
                                } else {
                                    getShape().startAlphaAnimation(new ShapeAnimatorRemoveListener(getShape()));
                                }
                            }

                            invalidate();
                        }
                    });

                    sh.addUpdateListener(new RippleOval.Listener() {
                        @Override
                        public void update() {
                            invalidate();
                        }
                    });
                    mOvalShapes.add(sh);
                    mCurrentShape = sh;
                    sh.start();

                    //  mTouched = true;
                    break;


                case MotionEvent.ACTION_MOVE:

                    if (!mDrawRect.contains((int)_x, (int)_y)){
                        // mTouched = false;
                        if (mCurrentShape!=null) {
                            mCurrentShape.startAlphaAnimation(new ShapeAnimatorRemoveListener(mCurrentShape));
                            mCurrentShape = null;
                        }
                        return false;
                    } else {

                        if (mRippleEdgeEffectType == RIPPLE_TYPE.STRONG) return false;

                        if (mCurrentShape!=null){
                            mCurrentShape.setXY((int)_x, (int)_y);
                            invalidate();
                        }
                    }


                    break;

                case MotionEvent.ACTION_UP:
                    final boolean needClick = mCurrentShape!=null && mClickListener!=null;
                    //mTouched = false;



                    if (needClick && mDrawRect.contains((int)_x, (int)_y)) mInternalClickListener.onClick(RippleEffectView.this);

                    if (mCurrentShape != null) {
                        mCurrentShape.startAlphaAnimation(new ShapeAnimatorRemoveListener(mCurrentShape));
                        mCurrentShape = null;
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    if (mCurrentShape!=null) {
                        mCurrentShape.startAlphaAnimation(new ShapeAnimatorRemoveListener(mCurrentShape));
                        mCurrentShape = null;
                    }
                    break;
            }

            return true;
        }
    };



    public RippleEffectView(Context context) {
        super(context);
        init(context);

    }

    public RippleEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context) {
        mRippleEnable = true;
        mDrawRect = new Rect(0, 0, 0, 0);
        mOvalShapes = new ArrayList<>();
        mRippleColor = RIPPLE_COLOR_DEFAULT;
        mStartAlpha = START_ALPHA_DEFAULT;
        mAnimationDuration = ANIMATION_DURATION_DEFAULT;
        mClickDelay = ANIMATION_DURATION_DEFAULT;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mReverse = false;
        mRippleEdgeEffectType = RIPPLE_TYPE.SOFT;
        super.setOnTouchListener(mInternalTouchListener);
    }

    private void init(Context context, AttributeSet attrs) {

        mDrawRect = new Rect(0, 0, 0, 0);
        mOvalShapes = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RippleEffectView,
                0, 0);

        try {
            mRippleColor = a.getColor(R.styleable.RippleEffectView_rippleColor, Color.argb(255, 127, 127, 127));
            mStartAlpha = a.getInt(R.styleable.RippleEffectView_rippleStartAlpha, 55);
            mAnimationDuration = a.getInt(R.styleable.RippleEffectView_rippleAnimationDuration, ANIMATION_DURATION_DEFAULT);
            mClickDelay = a.getInt(R.styleable.RippleEffectView_rippleClickDelay, mAnimationDuration);
            mReverse = a.getBoolean(R.styleable.RippleEffectView_rippleSoftReverse, false);
            mRippleEnable =a.getBoolean(R.styleable.RippleEffectView_rippleEnable, true);
            int type = a.getInt(R.styleable.RippleEffectView_rippleEdgeType, 0);
            switch (type){
                case 1:
                    mRippleEdgeEffectType = RIPPLE_TYPE.SOFT;
                    break;
                default:
                    mRippleEdgeEffectType = RIPPLE_TYPE.STRONG;
                    break;
            }
            //mFadeOutAfter = a.getBoolean(R.styleable.RippleEffectView_rippleFadeOutAfter, true);
        } finally {
            a.recycle();
        }

        super.setOnTouchListener(mInternalTouchListener);
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDrawRect.left = getPaddingLeft();
        mDrawRect.top = getPaddingTop();
        mDrawRect.right = getWidth() - getPaddingRight();
        mDrawRect.bottom = getHeight() - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (RippleOval animated : mOvalShapes) {
            Point point = animated.getPoint();
            switch (mRippleEdgeEffectType){
                case STRONG:
                    mPaint.setShader(null);
                    mPaint.setColor(mRippleColor);
                    mPaint.setAlpha(animated.getAlpha());
                    canvas.drawCircle(point.x, point.y, animated.getRadius(), mPaint);
                    break;
                case SOFT:
                    if (mReverse)
                        mPaint.setShader(new RadialGradient(point.x, point.y, animated.getRadius(), Color.TRANSPARENT, mRippleColor, Shader.TileMode.CLAMP));
                    else
                        mPaint.setShader(new RadialGradient(point.x, point.y, animated.getRadius(), mRippleColor, Color.TRANSPARENT, Shader.TileMode.CLAMP));
                    mPaint.setAlpha(animated.getAlpha());
                    canvas.drawRect(mDrawRect, mPaint);

                    break;
            }



        }
    }



    public void clear() {
        mOvalShapes.clear();
        invalidate();
    }


    private static class ShapeAnimatorListener implements Animator.AnimatorListener{

        private RippleOval shape;

        public void endAnimation(Animator animation){

        }

        public ShapeAnimatorListener(RippleOval shape) {
            this.shape = shape;
        }

        public RippleOval getShape() {
            return shape;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            endAnimation(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            endAnimation(animation);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }



    private class ShapeAnimatorRemoveListener extends ShapeAnimatorListener{
        public ShapeAnimatorRemoveListener(RippleOval shape) {
            super(shape);
        }

        @Override
        public void endAnimation(Animator animation) {
            super.endAnimation(animation);
            mOvalShapes.remove(getShape());
            postInvalidate();
        }
    }

}
