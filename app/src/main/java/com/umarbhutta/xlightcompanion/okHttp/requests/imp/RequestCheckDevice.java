package com.umarbhutta.xlightcompanion.okHttp.requests.imp;

import android.content.Context;
import android.util.Log;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AddDeviceResult;
import com.umarbhutta.xlightcompanion.okHttp.model.CheckDeviceResult;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by guangbinw on 2017/3/14.
 * 添加设备接口
 */
public class RequestCheckDevice implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private OnAddDeviceCallBack mOnAddDeviceCallBack;

    public static RequestCheckDevice getInstance() {
        return new RequestCheckDevice();
    }

    /**
     * 添加设备
     *
     * @param context
     * @param deviceId
     * @param mOnAddDeviceCallBack
     */
    public void checkDevice(Context context, String deviceId, OnAddDeviceCallBack mOnAddDeviceCallBack) {
        this.context = context;
        this.mOnAddDeviceCallBack = mOnAddDeviceCallBack;
        //if (UserUtils.isLogin(context)) {
        try {
            HttpUtils.getInstance().getRequestInfo(String.format(NetConfig.URL_CHECK_DEVICE, deviceId, UserUtils.getAccessToken(context)),
                    CheckDeviceResult.class, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
    }

    public boolean checkDeviceSync(String deviceId) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时时间;
                .build();
        Request request = new Request.Builder()
                .url(String.format(NetConfig.URL_CHECK_DEVICE, deviceId, UserUtils.getAccessToken(context)))
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            Log.e("XLight", response.body().toString());
            if (response.code() == 403) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        CheckDeviceResult info = (CheckDeviceResult) result;
        if (null != mOnAddDeviceCallBack) {
            mOnAddDeviceCallBack.mOnAddDeviceCallBackSuccess(info);
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mOnAddDeviceCallBack) {
            mOnAddDeviceCallBack.mOnAddDeviceCallBackFail(code, errMsg);
        }
    }

    public interface OnAddDeviceCallBack {
        void mOnAddDeviceCallBackFail(int code, String errMsg);

        void mOnAddDeviceCallBackSuccess(CheckDeviceResult device);
    }


}
