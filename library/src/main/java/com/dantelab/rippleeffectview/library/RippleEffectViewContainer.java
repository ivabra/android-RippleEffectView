package com.dantelab.rippleeffectview.library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * Created by ivan on 04.08.14.
 */
public class RippleEffectViewContainer extends FrameLayout {


    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private View mTopView;
    private RippleEffectView mRippleEffectView;
    // private WeakReference<OnClickListener> mClickListener;
    // private boolean isClicked = false;

    public RippleEffectViewContainer(Context context) {
        super(context);
        init();
    }

    public RippleEffectViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleEffectViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //mClickListener = new WeakReference<OnClickListener>(null);
        initRippleView();
    }
    private void initRippleView(){
        RippleEffectView rippleEffectView = new RippleEffectView(getContext());
        rippleEffectView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(rippleEffectView, 0);
        rippleEffectView.setId(R.id.rippleView);
        this.mRippleEffectView = rippleEffectView;
    }

    public RippleEffectView getRippleView(){
        return mRippleEffectView;
    }

    public void setTopView(final View v){
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null)
            parent.removeView(v);
        v.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(v);
        bringChildToFront(v);
        mTopView = v;
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        if (l==null) mRippleEffectView.setOnClickListener(null);
        else
            mRippleEffectView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onClick(RippleEffectViewContainer.this);
                }
            });
    }

    @Override
    public void setOnLongClickListener(final OnLongClickListener l) {
        if (l == null) mRippleEffectView.setOnLongClickListener(null);
        else
            mRippleEffectView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    l.onLongClick(RippleEffectViewContainer.this);
                    return false;
                }
            });
    }

    public View getTopView() {
        return mTopView;
    }



    public void bringRippleViewToFront() {
        bringChildToFront(mRippleEffectView);
    }

}
