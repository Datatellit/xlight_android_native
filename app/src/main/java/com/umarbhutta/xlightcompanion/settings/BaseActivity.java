package com.umarbhutta.xlightcompanion.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.views.ProgressDialogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

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
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
}
