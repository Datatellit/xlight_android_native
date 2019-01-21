package com.umarbhutta.xlightcompanion;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.umarbhutta.xlightcompanion.imgloader.ImageLoaderUtils;

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
        Log.d("XLight", "init bugly");
        CrashReport.initCrashReport(getApplicationContext());
//        final PushAgent mPushAgent = PushAgent.getInstance(this);
//        //注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(new IUmengRegisterCallback() {
//            @Override
//            public void onSuccess(String deviceToken) {
//                //注册成功会返回device token
//                Logger.d("XLight", "ument push register success deviceToken = " + deviceToken);
//                if (UserUtils.isLogin(getApplicationContext())) {
//                    //设置别名
//                    mPushAgent.addAlias(UserUtils.getUserInfo(getApplicationContext()).username, "XLight", new UTrack.ICallBack() {
//                        @Override
//                        public void onMessage(boolean b, String s) {
//                            Log.d("XLight", "umeng set alias success ,message:" + s);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(String s, String s1) {
//                Logger.d("XLight", "ument push register fail s = " + s + ", s1 = " + s1);
////                Log.e(TAG, "s=" + s + "::s1=" + s1);
//            }
//        });
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            //点击通知的自定义行为
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
//                if (msg.custom.equals("xxx") && mOnChangeViewCallback != null) {
//                    Log.d("XLight", "open share page");
//                }
//            }
//        };
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
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

    private static ChangeViewListener mOnChangeViewCallback;

    public static void setChangeViewListener(ChangeViewListener changeViewListener) {
        mOnChangeViewCallback = changeViewListener;
    }

    public interface ChangeViewListener {
        void onChangeViewListener(int id);
    }
}
