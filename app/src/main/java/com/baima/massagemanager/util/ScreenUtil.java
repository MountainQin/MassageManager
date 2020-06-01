package com.baima.massagemanager.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {
    /**
     * 获取 屏幕的宽高 数组，第一个是宽第二个是高
     *
     * @param context
     * @return
     */
    public static int[] getScreenWHPix(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        return new int[]{widthPixels, heightPixels};
    }

    /*
    获取 屏幕的宽度
     */
    public static int getScreenWidthPix(Context context) {
        return getScreenWHPix(context)[0];
    }

    /**
     * 获取 屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeightPix(Context context) {
        return getScreenWHPix(context)[1];
    }
}
