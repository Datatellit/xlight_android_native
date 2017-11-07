package com.umarbhutta.xlightcompanion.settings.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * create by：guangbinw on 16/4/1 15:31
 * email：guangbingwang@126.com
 * dp、px、sp相互转换
 */
public class DisplayUtils {

    /**
     * 和屏幕宽度16：9的高度
     */
    private static int nineHeight = -1;
    /**
     * 和屏幕宽度一半的16：9的高度
     */
    private static int nineHeight2 = -1;

    /**
     * 屏幕高度
     */
    private static int screenHeight = -1;
    /**
     * 屏幕宽度
     */
    private static int screenWidth = -1;

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取屏幕16：9的高度
     *
     * @return
     */
    public static int getImageHeight(Context context) {
        if (-1 != nineHeight) {
            return nineHeight;
        }
        int screenWidth = getScreenWidth(context);
        nineHeight = screenWidth * 9 / 16;
        return nineHeight;
    }

    /**
     * 获取宽度一半的16：9的高度
     *
     * @return
     */
    public static int getImageHalfHeight(Context context) {
        if (-1 != nineHeight2) {
            return nineHeight2;
        }
        int screenWidth = getScreenWidth(context)/2;
        nineHeight2 = screenWidth * 9 / 16;
        return nineHeight2;
    }

}
