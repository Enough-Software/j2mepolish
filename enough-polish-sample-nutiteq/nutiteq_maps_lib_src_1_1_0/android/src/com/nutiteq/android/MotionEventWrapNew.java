package com.nutiteq.android;

import android.os.Build;
import android.view.MotionEvent;

public class MotionEventWrapNew {
    private static final boolean IS_API_5 =  Integer.parseInt(Build.VERSION.SDK) >= 5;
    private MotionEventWrapNew(){};
    static int getPointerCount(MotionEvent event) {
        return IS_API_5 ? event.getPointerCount() : 1;
    }

    static float getX(MotionEvent event, int idx) {
        return IS_API_5 ? event.getX(idx) : 0;
    }

    static float getY(MotionEvent event, int idx) {
        return IS_API_5 ? event.getY(idx) : 0;
    }
}
