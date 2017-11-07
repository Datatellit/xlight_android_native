package com.umarbhutta.xlightcompanion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.umarbhutta.xlightcompanion.Tools.Logger;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class App extends Application {

    private List<Activity> activityList;
    public static App Inst;
    public static boolean isRequestBlue = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Inst = this;
        //初始化APP
        ImageLoaderUtils.initImageLoader(getApplicationContext());
        PushAgent mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.setDebugMode(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
//                Logger.i("ument push register success deviceToken = " + deviceToken);
//                Log.e(TAG, "deviceToken=" + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
//                Logger.i("ument push register fail s = " + s + ", s1 = " + s1);
//                Log.e(TAG, "s=" + s + "::s1=" + s1);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void setActivity(Activity activity) {
        if (activityList == null) {
            activityList = new ArrayList<Activity>();
        }
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    public void finishActivity() {
        if (activityList != null) {
            for (Activity activity : activityList) {
                activity.finish();
            }
        }
    }
}
