package com.umarbhutta.xlightcompanion.settings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;

/**
 * create by：guangbinw on 16/3/30 17:01
 * email：guangbingwang@126.com
 */
public class BaseActivity extends FragmentActivity {
    protected ProgressDialog mProgressDialog;
    private LayoutInflater mInflater;
    private View netErrorView;
    private String pageName = "";
    private boolean isRegistered = false;
    private NetBroadcastReceiver netWorkChangReceiver;
    private String TAG = "XLight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 20) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bar_color));
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mInflater = getLayoutInflater();

        //注册网络状态监听广播
        netWorkChangReceiver = new NetBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);
        isRegistered = true;
        //PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        if (TextUtils.isEmpty(pageName)) {
            pageName = getLocalClassName();
        }
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑
        if (isRegistered) {
            unregisterReceiver(netWorkChangReceiver);
        }
    }

    /**
     * 获得rootview
     *
     * @param context
     * @return
     */
    public View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content))
                .getChildAt(0);
    }

    /**
     * 替换fragment
     *
     * @param resid
     * @param frame
     */
    public void replace(int resid, Fragment frame) {
        if (findViewById(resid) != null) {
            FragmentTransaction t = this.getSupportFragmentManager()
                    .beginTransaction();
            t.replace(resid, frame);
            t.commitAllowingStateLoss();
        }
    }

    /**
     * 菊花消失
     */
    public void cancelProgressDialog() {
        if (mProgressDialog == null) {
            return;
        } else {
            mProgressDialog.cancel();
        }
    }

    /**
     * 菊花旋转
     */
    public void showProgressDialog(String msg) {

        if (null == mProgressDialog) {
            mProgressDialog = ProgressDialogUtils.showProgressDialog(this, msg);
        } else {
            mProgressDialog.setMessage(msg);
        }

        if (null != mProgressDialog) {
            mProgressDialog.show();
        }
    }

    // 自定义接口
    public interface NetEvevt {
        public void onNetChange(int netMobile);
    }

    private NetEvevt mNetEvent = null;

    public void setNetEvent(NetEvevt event) {
        this.mNetEvent = event;
    }

    /**
     * 自定义检查手机网络状态是否切换的广播接受器
     *
     * @author cj
     */
    public class NetBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // 如果相等的话就说明网络状态发生了变化
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                // 接口回调传过去状态的类型
                if (mNetEvent != null) {
                    int netWorkState = NetworkUtils.getNetworkType(context);
                    mNetEvent.onNetChange(netWorkState);
                }
            }
        }
    }

    /**
     * 判断是否是6.0以上的系统.很多的权限都不能自动或者提示开启. 功能可能需要多个权限,需要遍历判断
     * <p>
     * APP需要权限判断的地方(首页图灵聊天,个人中心AR扫描,修改头像, 设置祝福语音,视频,设置手机号码通讯录,AR,)
     *
     * @param dataPermission 需要的权限 ,数组
     * @return
     */
    public void selfPermissionGranted(Context context, PermissionCallback runnable, String[] dataPermission) {
        Log.i(TAG, "selfPermissionGranted");
        this.permissionRunnable = runnable;
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        boolean resultAll = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Build.VERSION.SDK_INT是获取当前手机版本   Build.VERSION_CODES.M为6.0系统
            Log.i(TAG, "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
            Log.i(TAG, "targetSdkVersion=" + targetSdkVersion);
            Log.i(TAG, "Build.VERSION_CODES.M=" + Build.VERSION_CODES.M);
            //如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M && checkPermissionGranted(context, dataPermission)) {//如果有权限
                if (permissionRunnable != null) {
                    permissionRunnable.hasPermission();
                    permissionRunnable = null;
                }
            } else {
                Log.i(TAG, "!checkPermissionGranted");
                //如果没有权限,请求
//判断用户是否已经拒绝过,直接去请求权限,而不管用户是否勾选了"不再询问",我们需要用户开启权限,没有权限就没有办法玩了
//      if (context.shouldShowRequestPermissionRationale(dataPermission[i])) {
//        context.requestPermissions(dataPermission, Constans.REQUEST_PERMISSION);//
//        MyLog.i(TAG, i + "+shouldShowRequestPermissionRationale=true" );//(1)只有"不在询问"才会到这一步
//    } else {
//        MyLog.i(TAG, i + "+shouldShowRequestPermissionRationale=false" );//(1)第一次原生进入 false (2)拒绝后,再次进入,还是提示false
//        isPermissionType(context, dataPermission[i]);
//    }
                requestPermissions(dataPermission, 100);
            }
        } else {
            Log.i(TAG, "Build.VERSION.SDK_INT<6.0");
            if (permissionRunnable != null) {
                permissionRunnable.hasPermission();
                permissionRunnable = null;
            }
        }
    }

    /**
     * 创建一个回调,方便判断权限的处理
     */
    private PermissionCallback permissionRunnable;

    public interface PermissionCallback {
        void hasPermission();

        void noPermission();
    }

    /**
     * 检测是否开启了权限  只要有一个权限没有打开,就返回false
     *
     * @param permissions
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)//这里需要用23
    public boolean checkPermissionGranted(Context context, String[] permissions) {
        Log.i(TAG, "checkPermissionGranted");
        boolean flag = true;
        for (String p : permissions) {
            Log.i(TAG, "permissions=" + p.toString());
            if (context.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }


    /**
     * 是否已经验证了权限
     *
     * @param grantResults
     * @return
     */
    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }
        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测权限的回调,是否开启了权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {//可能需要多个权限,需要遍历判断
                Log.i(TAG, "Permission=i=" + permissions[i] + "grantResults=" + grantResults.length);

                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {//如果点击开启权限
                    if (verifyPermissions(grantResults))
                        if (permissionRunnable != null) {
                            permissionRunnable.hasPermission();
                            permissionRunnable = null;
                        }
                } else {
                    Log.i(TAG, "Permission=" + permissions[i]);
                    if (permissionRunnable != null) {
                        permissionRunnable.noPermission();
                        permissionRunnable = null; //我在回调的地方,弹出自定义对话框,引导用户去开启权限
                    }
                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
