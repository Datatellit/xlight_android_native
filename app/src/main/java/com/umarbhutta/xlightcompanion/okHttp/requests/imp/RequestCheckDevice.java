package com.umarbhutta.xlightcompanion.okHttp.requests.imp;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AddDeviceResult;
import com.umarbhutta.xlightcompanion.okHttp.model.CheckDeviceResult;

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
        if (UserUtils.isLogin(context)) {
            try {
                HttpUtils.getInstance().getRequestInfo(String.format(NetConfig.URL_CHECK_DEVICE, deviceId, UserUtils.getUserInfo(context).getAccess_token()),
                        CheckDeviceResult.class, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
