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
import android.util.TypedValue;
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


    public static final int ANIMATION_DURATION_MIN = 150;
    public static final int ANIMATION_DURATION_MAX = 3000;



    private static final int DEF_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEF_ANIMATION_DURATION = 250;
    private static final int DEF_COLOR = Color.argb(100,127,127,127);
    private static final int DEF_CLICK_DELAY = 0;
    private static final int DEF_RIPPLE_RADIUS = -1;
    private static final RIPPLE_TYPE DEF_EDGE_TYPE = RIPPLE_TYPE.SOFT;
    private static final boolean DEF_REVERSE = false;
    private static final boolean DEF_ENABLE = true;
    private static final boolean DEF_SHOW_FINGER = false;
    private static final int DEF_FINGER_COLOR = Color.argb(55,127,127,127);

    private int DEF_FINGER_WIDTH(){
        return getResources().getDimensionPixelSize(R.dimen.ripple_finger_width);
    }
    private int DEF_FINGER_RADIUS(){
        return getResources().getDimensionPixelSize(R.dimen.ripple_dimen_radius);
    };


    private RIPPLE_TYPE mRippleEdgeEffectType;
    private ArrayList<RippleOval> mOvalShapes;
    private RippleOval mCurrentShape;

    private int mRippleColor;
    private int mFingerTouchWidth;
    private int mAnimationDuration;
    private long mClickDelay;
    private boolean mReverse;
    private boolean mRippleEnable;
    private int  mRippleFillColor;
    private int mRippleRadius;
    private float mFingerTouchRadius;
    private int mFingerTouchColor;
    private boolean mFingerTouchShow;


    private Rect mDrawRect;

    private Paint mPaint;

    private OnClickListener mClickListener;
    private OnTouchListener mTouchListener;
    private OnLongClickListener mLongClickListener;


    public boolean isShowFingerTouch() {
        return mFingerTouchShow;
    }

    public void setShowFingerTouch(boolean showFingerTouch) {
        this.mFingerTouchShow = showFingerTouch;

        invalidate();
    }

    public int getFingerTouchColor() {
        return mFingerTouchColor;
    }

    public void setFingerTouchColor(int fingerTouchColor) {
        this.mFingerTouchColor = fingerTouchColor;
        invalidate();
    }

    public float getFingerTouchRadius() {
        return mFingerTouchRadius;
    }

    public void setFingerTouchRadius(float fingerTouchRadius) {
        this.mFingerTouchRadius = fingerTouchRadius;
        invalidate();
    }

    public void setRippleRadius(int rippleRadius) {
        this.mRippleRadius = rippleRadius;
        invalidate();
    }

    public int getRippleRadius() {
        return mRippleRadius;
    }

    public void setRippleFillColor(int color){
        this.mRippleFillColor = color;
        invalidate();
    }

    public void setReverse(boolean reverse) {
        this.mReverse = reverse;
       invalidate();
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
    }

    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setColorAlpha(int alpha) {
        this.mRippleColor = Color.argb(alpha, Color.red(mRippleColor), Color.green(mRippleColor), Color.blue(mRippleColor));
        invalidate();
    }

    public int getColorAlpha() {
        return Color.alpha(mRippleColor);
    }

    public void setClickDelay(int clickdelay) {
        this.mClickDelay = clickdelay;
    }

    public long getClickDelay() {
        return mClickDelay;
    }

    public void setRippleColor(int rippleColor) {
        this.mRippleColor = rippleColor;
        invalidate();
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

    private static int colorWithAlpha(int color, int alpha){
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private static Shader createShader(int radius, int color, boolean reverse){
            if (radius <= 0) return null;
            if (reverse){
                return new RadialGradient(0,0,radius, new int[]{Color.TRANSPARENT, color}, new float[]{0.f, 1.0f}, Shader.TileMode.CLAMP);
            } else {
                return new RadialGradient(0,0,radius, new int[]{ color,  Color.TRANSPARENT}, new float[]{0.f, 1.0f}, Shader.TileMode.CLAMP);
            }
    }

    private static Shader createShader(int radius, int color, boolean reverse, int x, int y) {
        if (radius <= 0) return null;
        if (reverse){
            return new RadialGradient(x,y,radius, new int[]{Color.TRANSPARENT, color}, new float[]{0.f, 1.0f}, Shader.TileMode.CLAMP);
        } else {
            return new RadialGradient(x,y,radius, new int[]{ color,  Color.TRANSPARENT}, new float[]{0.f, 1.0f}, Shader.TileMode.CLAMP);
        }
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
                    float radius;
                    if (mRippleRadius < 0) {
                        float w = getWidth();
                        float h = getHeight();
                        radius = (float) Math.sqrt(w * w + h * h);
                    } else {
                        radius = mRippleRadius;
                    }
                    final RippleOval sh = new RippleOval((int) _x, (int) _y, 0, radius, Color.alpha(mRippleColor), Color.alpha(mRippleFillColor), mAnimationDuration);
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

                        if (mRippleEdgeEffectType == RIPPLE_TYPE.SOFT || mFingerTouchShow)
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
        mRippleColor = DEF_COLOR;
        mAnimationDuration = DEF_ANIMATION_DURATION;
        mClickDelay = DEF_CLICK_DELAY;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mReverse = DEF_REVERSE;
        mRippleEnable = DEF_ENABLE;
        mRippleFillColor = DEF_FILL_COLOR;
        mRippleEdgeEffectType = DEF_EDGE_TYPE;
        mRippleRadius = DEF_RIPPLE_RADIUS;
        mFingerTouchShow = DEF_SHOW_FINGER;
        mFingerTouchRadius = DEF_FINGER_RADIUS();
        mFingerTouchColor = DEF_FINGER_COLOR;
        mFingerTouchWidth = DEF_FINGER_WIDTH();
        super.setOnTouchListener(mInternalTouchListener);
    }

    private void init(Context context, AttributeSet attrs) {

        mDrawRect = new Rect(0, 0, 0, 0);
        mOvalShapes = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RippleEffectView,
                0, 0);

        try {
            mRippleColor = a.getColor(R.styleable.RippleEffectView_rippleColor, DEF_COLOR);
            mAnimationDuration = a.getInt(R.styleable.RippleEffectView_rippleAnimationDuration, DEF_ANIMATION_DURATION);
            mClickDelay = a.getInt(R.styleable.RippleEffectView_rippleClickDelay, DEF_CLICK_DELAY);
            mReverse = a.getBoolean(R.styleable.RippleEffectView_rippleSoftReverse, DEF_REVERSE);
            mRippleEnable =a.getBoolean(R.styleable.RippleEffectView_rippleEnable, DEF_ENABLE);
            mRippleFillColor = a.getColor(R.styleable.RippleEffectView_rippleFillColor, DEF_FILL_COLOR);
            mFingerTouchShow = a.getBoolean(R.styleable.RippleEffectView_rippleShowFinger, DEF_SHOW_FINGER);
            mFingerTouchColor = a.getColor(R.styleable.RippleEffectView_rippleFingerColor, DEF_FINGER_COLOR);
            mFingerTouchRadius = a.getDimensionPixelSize(R.styleable.RippleEffectView_rippleFingerRadius, DEF_FINGER_RADIUS());
            mFingerTouchWidth = a.getDimensionPixelSize(R.styleable.RippleEffectView_rippleFingerWidth, DEF_FINGER_WIDTH());

            TypedValue val = new TypedValue();
            if (a.getValue(R.styleable.RippleEffectView_rippleRadius, val)){
                if (val.type == TypedValue.TYPE_INT_DEC){
                    mRippleRadius = DEF_RIPPLE_RADIUS;
                } else {
                    mRippleRadius =a.getDimensionPixelSize(R.styleable.RippleEffectView_rippleRadius, -1);
                }
            } else {
                mRippleRadius = DEF_RIPPLE_RADIUS;
            }

            int type = a.getInt(R.styleable.RippleEffectView_rippleEdgeType, -1);

            switch (type){
                case 1:
                    mRippleEdgeEffectType = RIPPLE_TYPE.SOFT;
                    break;
                case 0:
                    mRippleEdgeEffectType = RIPPLE_TYPE.STRONG;
                    break;
                default:
                    mRippleEdgeEffectType = DEF_EDGE_TYPE;
                    break;
            }
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
        mPaint.setStyle(Paint.Style.FILL);
        for (RippleOval shape : mOvalShapes) {
            if (mRippleFillColor!=Color.TRANSPARENT)
            drawBackground(canvas, shape);
            drawShader(canvas, shape);
            if (mFingerTouchShow){
                drawFinger(canvas, shape);
            }
        }
    }


    private void drawBackground(Canvas canvas, RippleOval shape){
            mPaint.setShader(null);
            mPaint.setColor(mRippleFillColor);
            mPaint.setAlpha(shape.getBackgroundAlpha());
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mDrawRect, mPaint);
    }

    private void drawShader(Canvas canvas, RippleOval shape){
        Point point = shape.getPoint();
        switch (mRippleEdgeEffectType){
            case STRONG:
                mPaint.setShader(null);
                mPaint.setColor(mRippleColor);
                mPaint.setAlpha(shape.getShaderAlpha());
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(point.x, point.y, shape.getRadius(), mPaint);
                break;
            case SOFT:

                Shader shader = createShader((int)shape.getRadius(), mRippleColor, mReverse, point.x, point.y);
                if (shader == null) return;
                mPaint.setShader(shader);
                mPaint.setAlpha(shape.getShaderAlpha());
                canvas.drawRect(mDrawRect, mPaint);
                break;
        }
    }



    private void drawFinger(Canvas canvas, RippleOval shape){
        Point point = shape.getPoint();
        mPaint.setShader(null);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(mFingerTouchColor);
        mPaint.setAlpha(shape.getShaderAlpha());
        mPaint.setStrokeWidth(mFingerTouchWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(point.x, point.y, mFingerTouchRadius * shape.getInAnimationFraction(), mPaint);
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
