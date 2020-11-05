package com.ozonetech.ozochat.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by bitu on 17/8/17.
 */

public class DeviceScreenUtil {
    private static DeviceScreenUtil deviceScreenModel;
    private Rect deviceRect;
    private float density;
    private float scaledDensity;
    private float xdpi;
    private int densityDpi;
    private DisplayMetrics displayMetrics;

    public DeviceScreenUtil(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
        this.deviceRect = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        this.density = displayMetrics.density;
        this.xdpi = displayMetrics.xdpi;
        this.scaledDensity = displayMetrics.scaledDensity;
        this.densityDpi = displayMetrics.densityDpi;
    }

    public DisplayMetrics getDisplayMetrics () {
        return displayMetrics;
    }

    public static DeviceScreenUtil getInstance () {
        if (deviceScreenModel == null) {
            deviceScreenModel = new DeviceScreenUtil(Resources.getSystem().getDisplayMetrics());
        }
        return deviceScreenModel;
    }

    public Rect getDeviceRect () {
        return deviceRect;
    }

    public float getDensity () {
        return density;
    }


    public float getScaledDensity () {
        return scaledDensity;
    }


    public int getDensityDpi () {
        return densityDpi;
    }


    public int convertDpToPixel (float dp) {
        return Math.round(dp * density);
    }


    public int getWidth () {
        return deviceRect.width();
    }

    public int getWidth (int width, float value) {
        int v = (int) value;
        return (width * v);
    }

    public int getWidth (float percent) {
        return Math.round(deviceRect.width() * percent);
    }

    public int getHeight (float percent) {
        return Math.round(deviceRect.height() * percent);
    }


    public LinearLayout.LayoutParams getLinearLayoutParams (LinearLayout linearLayout,
                                                            int width,
                                                            int left,
                                                            int top,
                                                            int right,
                                                            int bottom) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        linearLayout.setPadding(left, top, right, bottom);
        return layoutParams;
    }

    public RelativeLayout.LayoutParams getRelativeLayoutParams(RelativeLayout linearLayout,
                                                               int height, int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = height;
        layoutParams.height = height;
        linearLayout.setPadding(left, top, right, bottom);
        return layoutParams;
    }


}
