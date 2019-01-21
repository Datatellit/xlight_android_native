package com.umarbhutta.xlightcompanion.settings.utils;

//import android.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.umarbhutta.xlightcompanion.Tools.NetworkUtils;
import com.umarbhutta.xlightcompanion.settings.BaseActivity;

public class BaseFragment extends Fragment {
    private boolean isRegistered = false;
    private NetBroadcastReceiver netWorkChangReceiver;
    private String TAG = "XLight";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册网络状态监听广播
        IntentFilter filter = new IntentFilter();
        netWorkChangReceiver = new NetBroadcastReceiver();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (!isRegistered) {
            getActivity().registerReceiver(netWorkChangReceiver, filter);
        }
        isRegistered = true;
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
     * Android M运行时权限请求封装
     *
     * @param runnable    请求权限回调
     * @param permissions 请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void selfPermissionGranted(Context context, BaseActivity.PermissionCallback runnable, String... permissions) {
        Log.i(TAG, "selfPermissionGranted");
        if (context != null && context instanceof BaseActivity) {
            Log.i(TAG, "getActivity()!=null");
            ((BaseActivity) getActivity()).selfPermissionGranted(context, runnable, permissions);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(netWorkChangReceiver);
    }
}
