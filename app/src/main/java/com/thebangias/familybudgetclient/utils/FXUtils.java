package com.thebangias.familybudgetclient.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.thebangias.familybudgetclient.R;

/**
 * Utilities for animating controls
 */
public class FXUtils {

    public static void slideDown(Context ctx, View v, Animation.AnimationListener listener) {
        // load the animation
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);

        // if a listener was provided, attach it now
        if (listener != null) {
            a.setAnimationListener(listener);
        }

        // perform the animation
        doAnimation(a, v);
    }

    public static void slideUp(Context ctx, View v, Animation.AnimationListener listener) {
        // load the animation
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);

        // if a listener was provided, attach it now
        if (listener != null) {
            a.setAnimationListener(listener);
        }

        // perform the animation
        doAnimation(a, v);
    }

    private static void doAnimation(Animation a, View v) {

        // perform the animation given a, v
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
}
