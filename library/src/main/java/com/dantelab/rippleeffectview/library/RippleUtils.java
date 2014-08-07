package com.dantelab.rippleeffectview.library;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by ivan on 05.08.14.
 */
public final class RippleUtils {


    public static RippleEffectViewContainer setRippleView(View v, boolean foreground, boolean saveId) {

        RippleEffectViewContainer frame = new RippleEffectViewContainer(v.getContext());
        frame.setLayoutParams(v.getLayoutParams());

        if (saveId)
        frame.setId(v.getId());

        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeView(v);

        }

        frame.setTopView(v);

        if (foreground)frame.bringRippleViewToFront();


        if (parent != null)
            parent.addView(frame);
        return frame;
    }

    public static RippleEffectViewContainer setRippleView(View v){
        return setRippleView(v, false, false);
    }


}
