package com.umarbhutta.xlightcompanion.Tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.umarbhutta.xlightcompanion.help.DeviceInfo;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousParams;
import com.umarbhutta.xlightcompanion.okHttp.model.AnonymousResult;
import com.umarbhutta.xlightcompanion.okHttp.model.LoginResult;

import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    public static boolean checkTokenValid(String token) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间;
                .build();
        Request request = new Request.Builder()
                .url(NetConfig.URL_DEVICE_DETAIL_INFO + "?access_token=" + token)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            Log.e("XLight", response.body().string());
            JSONObject jsonObject = new JSONObject(response.body().string());
            if (jsonObject.getInt("code") == 10000 || jsonObject.getInt("code") == 10002) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            Log.e("XLight", ex.getMessage());
            return false;
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
        return (LoginResult) SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__USERINFO, null);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static AnonymousParams getAnonymousInfo(Context context) {
        try {
            return (AnonymousParams) SharedPreferencesUtils.getObject(context, SharedPreferencesUtils.KEY__ANONYMOUSINFO, null);
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
