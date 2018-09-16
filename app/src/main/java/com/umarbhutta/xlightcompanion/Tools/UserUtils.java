package com.umarbhutta.xlightcompanion.Tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.umarbhutta.xlightcompanion.help.DeviceInfo;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousResult;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;

import java.util.Date;
import java.util.Objects;

/**
 * Created by guangbinw on 2017/3/12.
 */

public class UserUtils {
    public static AnonymousParams anonymous;

    /**
     * 判断是否登录
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        LoginResult result = (LoginResult) SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__USERINFO, null);
        if (null == result || TextUtils.isEmpty(result.access_token) || TextUtils.isEmpty(result.username)) {
            Log.d("XLight", "user not login");
            return false;
        }
        return true;
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpires(Context context, String key) {
        Object result = SharedPreferencesUtils.getObject(context, key, null);
        //立即重新登录
        if (result == null)
            return true;
        Log.e("XLight", result.toString());
        //判断是否过期
        Date curTime = new Date();
        curTime.setTime(new Date().getTime() - 86400000);
        if (key == SharedPreferencesUtils.KEY__ANONYMOUSINFO) {
            if (((AnonymousParams) result).expires != null && curTime.getTime() > ((AnonymousParams) result).expires.getTime()) {
                // 进行登录
                return true;
            } else {
                return true;
            }
        } else {
            if (curTime.getTime() > ((LoginResult) result).expires.getTime()) {
                // 进行登录
                return true;
            }
        }
        return false;
    }

    /**
     * 设置用户信息
     *
     * @param context
     * @param userInfo
     */
    public static void saveUserInfo(Context context, LoginResult userInfo) {
        SharedPreferencesUtils.putObject(context, SharedPreferencesUtils.KEY__USERINFO, userInfo);
    }

    /**
     * 设置用户信息
     *
     * @param context
     * @param ar
     */
    public static void saveAnonymousInfo(Context context, AnonymousResult ar) {
        SharedPreferencesUtils.putObject(context, SharedPreferencesUtils.KEY__ANONYMOUSINFO, ar.data);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static LoginResult getUserInfo(Context context) {
        return (LoginResult) SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__USERINFO, null);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static AnonymousParams getAnonymousInfo(Context context) {
        return (AnonymousParams) SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__ANONYMOUSINFO, null);
    }

    public static AnonymousParams getAnonymous(Context context) {
        AnonymousParams anonymousParams = new AnonymousParams();
        anonymousParams.imei = DeviceInfo.getIMEI(context);
        try {
            anonymousParams.bluetoothMac = DeviceInfo.getBluetoothMAC();
        } catch (Exception e) {
            anonymousParams.bluetoothMac = "00:00:00:00:00:00";
        }
        anonymousParams.hardwareInfo = DeviceInfo.getHardwareInfo();
        anonymousParams.systemInfo = DeviceInfo.getSystemInfo();
        anonymousParams.type = 1;
        anonymousParams.uniqueId = DeviceInfo.getSign(context);
        anonymousParams.wlanMac = DeviceInfo.getWlanMAC(context);
        return anonymousParams;
    }

    public static String getAccessToken(Context context) {
        if (isLogin(context)) {
            return getUserInfo(context).access_token;
        } else {
            return getAnonymousInfo(context).access_token;
        }
    }
}
