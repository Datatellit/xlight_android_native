package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SensorsResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Sensorsdata;

import java.util.Arrays;
import java.util.List;

/**
 * Created by waroom on 2017/10/30.
 * 获取默认环境信息
 */
public class RequestSensorInfo implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private OnRequestSensorInfoCallback mRequestSensorInfo;

    public static RequestSensorInfo getInstance(Context context) {
        return new RequestSensorInfo(context);
    }

    private RequestSensorInfo(Context context) {
        this.context = context;
    }

    /**
     * 获取信息
     */
    public void getBaseInfo(OnRequestSensorInfoCallback mRequestSensorInfo, List<String> devices) {
        this.mRequestSensorInfo = mRequestSensorInfo;
        String strDevices = "";
        for (int i = 0, j = devices.size(); i < j; i++) {
            strDevices = strDevices + "deviceId[]=" + devices.get(i) + "&";
        }
        if (strDevices.length() > 1) {
            strDevices = strDevices.substring(0, strDevices.length() - 1);
        }

        if (UserUtils.isLogin(context)) {
            HttpUtils.getInstance().getRequestInfo(NetConfig.URL_GET_SENSOR + UserUtils.getUserInfo(context).getAccess_token() + "&" + strDevices,
                    SensorsResult.class, this);
        }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        SensorsResult info = (SensorsResult) result;

        if (info.code == 1) {
            if (null != mRequestSensorInfo) {
                mRequestSensorInfo.onRequestSensorInfoSuccess(info.data);
            }
        } else {
            if (null != mRequestSensorInfo) {
                mRequestSensorInfo.onRequestSensorInfoFail(info.code, info.msg);
            }
        }

    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mRequestSensorInfo) {
            mRequestSensorInfo.onRequestSensorInfoFail(NetConfig.ERROR_NET_ERROT, context.getString(R.string.net_error));
        }
    }

    public interface OnRequestSensorInfoCallback {
        void onRequestSensorInfoSuccess(List<Sensorsdata> mSensorsdata);

        void onRequestSensorInfoFail(int code, String errMsg);
    }

}
