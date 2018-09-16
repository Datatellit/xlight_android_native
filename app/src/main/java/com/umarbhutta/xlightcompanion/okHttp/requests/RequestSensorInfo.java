package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.SensorsResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Sensorsdata;

import org.json.JSONObject;

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
    public void getBaseInfo(List<String> devices, OnRequestSensorInfoCallback mRequestSensorInfo) {
        this.mRequestSensorInfo = mRequestSensorInfo;
        String strDevices = "";
        for (int i = 0, j = devices.size(); i < j; i++) {
            strDevices = strDevices + "coreid[]=" + devices.get(i) + "&";
        }
        if (strDevices.length() > 1) {
            strDevices = strDevices.substring(0, strDevices.length() - 1);
        }

        //if (UserUtils.isLogin(context)) {
        HttpUtils.getInstance().getRequestInfo(String.format(NetConfig.URL_GET_SENSOR, UserUtils.getAccessToken(context)) + "&" + strDevices,
                null, this);
        //}
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        try {
            JSONObject info = new JSONObject(result.toString());

            if (info.getInt("code") == 1) {
                if (null != mRequestSensorInfo) {
                    mRequestSensorInfo.onRequestSensorInfoSuccess(info.getJSONObject("data"));
                }
            } else {
                if (null != mRequestSensorInfo) {
                    mRequestSensorInfo.onRequestSensorInfoFail(info.getInt("code"), info.getString("msg"));
                }
            }
        } catch (Exception ex) {
            if (null != mRequestSensorInfo) {
                mRequestSensorInfo.onRequestSensorInfoFail(0, ex.getMessage());
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
        void onRequestSensorInfoSuccess(JSONObject mSensorsdata);

        void onRequestSensorInfoFail(int code, String errMsg);
    }

}
