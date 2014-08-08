package com.dantelab.rippleeffectview.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by ivan on 04.08.14.
 */
class RippleOval {
   // private static int DEF_ANIMATION_DURATION = 150;
    private Point mPoint;
    private float endRadius;
    private float startRadius;
    private float mRadius;
    private int mAnimationDuration;
    private ValueAnimator mValueAnimator;
    private Listener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private int startAlpha;
    private int mShaderAlpha;
    private int mBackgroundAlpha;
    private int backgroundStartAlpha;
    private boolean dismissAfrer;
    private float mInAnimationValue;
    private float mFadeAnimationValue;
    //private Shader shader;

    public RippleOval(int x, int y, float startRadius, float endRadius,  int startAlpha, int backgroundStartAlpha, int animationDuration) {
        init(x,y, startRadius, endRadius, startAlpha, animationDuration);
        this.backgroundStartAlpha = mBackgroundAlpha= backgroundStartAlpha;
    }


    public RippleOval(int x, int y, float startRadius, float endRadius,  int startAlpha, int animationDuration) {
        init(x,y, startRadius, endRadius, startAlpha, animationDuration);
    }


    public RippleOval(int x, int y, final float endRadius, int startAlpha, int animationDuration) {
        init(x,y, 0, endRadius, startAlpha, animationDuration);
    }

    private void init(int x, int y,final float startRadius, final float endRadius, int startAlpha, int animationDuration){
        mPoint = new Point(x, y);
        this.startAlpha = mShaderAlpha = startAlpha;
        mRadius = this.startRadius = startRadius;
        this.endRadius = endRadius;
        dismissAfrer = true;
        mAnimationDuration = animationDuration;

        mInAnimationValue = 0;
        mFadeAnimationValue = 0;

      //  if (hasShader) mMatrix = new Matrix();

        mValueAnimator = ValueAnimator.ofFloat(0,1);
        mValueAnimator.setDuration(animationDuration);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mInAnimationValue = (float) valueAnimator.getAnimatedValue();
                computeScaleAndAlpha();
                notifyUpdateListener();
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (mAnimatorListener != null) mAnimatorListener.onAnimationStart(animator);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mInAnimationValue = 1;
                mFadeAnimationValue = 0;

                computeScaleAndAlpha();

                if (dismissAfrer)
                    initAlphaAnimator();
                else {
                    if (mAnimatorListener != null) mAnimatorListener.onAnimationEnd(animator);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mInAnimationValue = 1;
                mFadeAnimationValue = 0;

                computeScaleAndAlpha();

                if (dismissAfrer)
                    initAlphaAnimator();
                else {
                    if (mAnimatorListener != null) mAnimatorListener.onAnimationEnd(animator);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    void computeScaleAndAlpha(){
       mRadius = startRadius + Math.round((endRadius - startRadius)* mInAnimationValue);
        mShaderAlpha = Math.round(startAlpha * (1-mFadeAnimationValue));
        mBackgroundAlpha = Math.round(backgroundStartAlpha * Math.max(1- mInAnimationValue - mFadeAnimationValue, 0));
      //  Log.d("ripple", "radius: " + mRadius + " shaderAlpha: " + mShaderAlpha + " bgAlpha: " + mBackgroundAlpha  + " IValue: " + mInAnimationValue);
    }




    private void initAlphaAnimator(){

        ValueAnimator newAnimator = ValueAnimator.ofFloat(0, 1);
        newAnimator.setDuration((long) (mAnimationDuration*0.5));

        newAnimator.setInterpolator(new LinearInterpolator());
        newAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mFadeAnimationValue = (float) valueAnimator.getAnimatedValue();
                computeScaleAndAlpha();
                notifyUpdateListener();
            }
        });
        newAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mFadeAnimationValue =1;
                computeScaleAndAlpha();
                if (mAnimatorListener != null) mAnimatorListener.onAnimationEnd(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mFadeAnimationValue = 1;
                computeScaleAndAlpha();
                if (mAnimatorListener != null) mAnimatorListener.onAnimationCancel(animator);

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        if (mValueAnimator!=null){
            mValueAnimator.removeAllListeners();
        }
        newAnimator.start();
        mValueAnimator = newAnimator;
    }

    private void notifyUpdateListener(){
        if (mUpdateListener!=null)mUpdateListener.update();
    }

    public Point getPoint() {
        return mPoint;
    }

    public float getRadius(){
        return mRadius;
    }

    public void start(){
        mValueAnimator.start();
    }

    public void cancel(){
        mValueAnimator.cancel();
    }

    public void end(){
        mValueAnimator.end();
    }

    public void setAnimatorListener(Animator.AnimatorListener listener){
        mAnimatorListener = listener;
    }

    public void addUpdateListener(Listener listener) {
        mUpdateListener = listener;
    }


    public void setFadeOutAfter(boolean b) {
        dismissAfrer = b;
    }

    public void startAlphaAnimation(Animator.AnimatorListener animatorListener) {
        mAnimatorListener = null;
        initAlphaAnimator();
        mAnimatorListener = animatorListener;

    }

    public void setXY(int x, int y) {
        mPoint.set(x, y);
    }

    public float getInAnimationFraction() {
        return mInAnimationValue;
    }

    public float getFadeAnimationFraction() {
        return mFadeAnimationValue;
    }


    public int getStartAlpha() {
        return startAlpha;
    }

    public float getEndRadius() {
        return endRadius;
    }

//    public void setShader(Shader shader) {
//        this.shader = shader;
//    }


    public static interface Listener{
        void update();
    }

    public int getShaderAlpha() {
        return mShaderAlpha;
    }

    public int getBackgroundAlpha() {
        return mBackgroundAlpha;
    }

//    public Shader getShader() {
//        return shader;
//    }
}
