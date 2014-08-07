package com.dantelab.rippleeffectview.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;

/**
 * Created by ivan on 04.08.14.
 */
class RippleOval {
   // private static int ANIMATION_DURATION_DEFAULT = 150;
    private Point mPoint;
    private float mStartRadius, mEndRadius;
    private float mRadius;
    private int mAnimationDuration;
    private ValueAnimator mValueAnimator;
    private Listener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private int mStartAlpha;
    private int mAlpha;
    private boolean mDismissAfrer;

    public RippleOval(int x, int y, final float startRadius, final float endRadius, int startAlpha, int animationDuration) {
        mPoint = new Point(x, y);
        mStartAlpha = mAlpha = startAlpha;
        mStartRadius = mRadius = startRadius;
        mEndRadius = endRadius;
        mDismissAfrer = true;
        mAnimationDuration = animationDuration;

        mValueAnimator = ValueAnimator.ofFloat(mStartRadius,mEndRadius);
        mValueAnimator.setDuration(animationDuration);
        mValueAnimator.setInterpolator(new DecelerateInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRadius = (float) valueAnimator.getAnimatedValue();
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
                if (mDismissAfrer)
                initAlphaAnimator();
                else {
                    if (mAnimatorListener != null) mAnimatorListener.onAnimationEnd(animator);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (mAnimatorListener != null) mAnimatorListener.onAnimationCancel(animator);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private void initAlphaAnimator(){

        ValueAnimator newAnimator = ValueAnimator.ofInt(mStartAlpha, 0);
        newAnimator.setDuration((long) (mAnimationDuration*0.5));

        newAnimator.setInterpolator(new DecelerateInterpolator());
        newAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAlpha = (int) valueAnimator.getAnimatedValue();
                notifyUpdateListener();
            }
        });
        newAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mAnimatorListener != null) mAnimatorListener.onAnimationEnd(animator);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (mAnimatorListener != null) mAnimatorListener.onAnimationCancel(animator);

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        newAnimator.start();
        if (mValueAnimator!=null){
            mValueAnimator.removeAllListeners();
        }
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

    public int getAlpha() {
        return mAlpha;
    }

    public void setFadeOutAfter(boolean b) {
        mDismissAfrer = b;
    }

    public void startAlphaAnimation(Animator.AnimatorListener animatorListener) {

        mAnimatorListener = null;
        initAlphaAnimator();
        mAnimatorListener = animatorListener;

    }

    public void setXY(int x, int y) {
        mPoint.set(x, y);
    }


    public interface Listener{
        void update();
    }

    public float getStartRadius() {
        return mStartRadius;
    }

    public int getStartAlpha() {
        return mStartAlpha;
    }

    public float getEndRadius() {
        return mEndRadius;
    }
}
