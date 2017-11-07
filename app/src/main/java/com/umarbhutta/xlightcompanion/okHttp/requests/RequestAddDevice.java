package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.AddDeviceResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guangbinw on 2017/3/14.
 * 添加设备接口
 */
public class RequestAddDevice implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private OnAddDeviceCallBack mOnAddDeviceCallBack;

    public static RequestAddDevice getInstance() {
        return new RequestAddDevice();
    }

    /**
     * 添加设备
     *
     * @param context
     * @param jsonObject
     * @param mOnAddDeviceCallBack
     */
    public void addDevice(Context context, String jsonObject, OnAddDeviceCallBack mOnAddDeviceCallBack) {
        this.context = context;
        this.mOnAddDeviceCallBack = mOnAddDeviceCallBack;
        if (UserUtils.isLogin(context)) {
            try {
                HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ADD_DEVICE + UserUtils.getUserInfo(context).getAccess_token(),
                        jsonObject, AddDeviceResult.class, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        AddDeviceResult infos = (AddDeviceResult) result;
        if (null != mOnAddDeviceCallBack) {
            mOnAddDeviceCallBack.mOnAddDeviceCallBackSuccess(infos.code);
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

        void mOnAddDeviceCallBackSuccess(int code);
    }


}
