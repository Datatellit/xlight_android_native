package com.umarbhutta.xlightcompanion.Tools;

import android.util.Log;

import com.umarbhutta.xlightcompanion.okHttp.NetConfig;


/**
 * Log统一管理类
 * <p>
 * Created by guangbinw on 2017/3/12.
 */
public class Logger {
    public static final String TAG = "light";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (NetConfig.isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (NetConfig.isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (NetConfig.isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (NetConfig.isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (NetConfig.isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (NetConfig.isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (NetConfig.isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (NetConfig.isDebug)
            Log.v(tag, msg);
    }
}
