package io.particle.android.sdk.devicesetup.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;


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
        LoginResult result = null;
        try {
            Object obj = SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__USERINFO, null);
            if (obj != null) {
                result = new LoginResult();
                JSONObject json = new JSONObject(new Gson().toJson(obj));
                result.access_token = json.getString("access_token");
                result.username = json.getString("username");
            }
            if (null == result || TextUtils.isEmpty(result.access_token) || TextUtils.isEmpty(result.username)) {
                Log.d("XLight", "user not login");
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
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
                // 进行有效性的check，如果不通过，则返回true
//                if (checkTokenValid(((AnonymousParams) result).access_token)) {
//                    return false;
//                } else {
//                    return true;
//                }
                return false;
            }
        } else {
            if (curTime.getTime() > ((LoginResult) result).expires.getTime()) {
                // 进行登录
                return true;
            } else {
                // 进行有效性的check，如果不通过，则返回true
//                if (checkTokenValid(((LoginResult) result).access_token)) {
//                    return false;
//                } else {
//                    return true;
//                }
                return false;
            }
        }
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
        LoginResult loginResult = null;
        try {
            Object obj = SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__USERINFO, null);
            if (obj != null) {
                loginResult = new LoginResult();
                JSONObject json = new JSONObject(new Gson().toJson(obj));
                loginResult.access_token = json.getString("access_token");
                loginResult.id = json.getInt("id");
            }
            return loginResult;
        } catch (Exception ex) {
            return new LoginResult();
        }
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static AnonymousParams getAnonymousInfo(Context context) {
        AnonymousParams anonymousParams = null;
        try {
            Object obj = SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__ANONYMOUSINFO, null);
            if (obj != null) {
                JSONObject json = new JSONObject(new Gson().toJson(obj));
                anonymousParams = new AnonymousParams();
                anonymousParams.access_token = json.getString("access_token");
                anonymousParams.id = json.getInt("id");
            }
            return anonymousParams;
        } catch (Exception ex) {
            return new AnonymousParams();
        }
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
