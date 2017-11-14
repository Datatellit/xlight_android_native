package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;

/**
 * Created by guangbinw on 2017/3/14.
 * 场景详情接口
 */
public class RequestScenceDetaillInfo implements HttpUtils.OnHttpRequestCallBack {
    private OnRequestFirstPageInfoCallback mOnRequestFirstPageInfoCallback;

    public static RequestScenceDetaillInfo getInstance() {
        return new RequestScenceDetaillInfo();
    }

    /**
     * 获取场景详情
     *
     * @param context
     * @param deviceId
     */
    public void getSceneInfo(Context context, int deviceId, OnRequestFirstPageInfoCallback mOnRequestFirstPageInfoCallback) {
        this.mOnRequestFirstPageInfoCallback = mOnRequestFirstPageInfoCallback;
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_SCENE_DETAIL + deviceId + "?access_token=" + UserUtils.getAccessToken(context), DeviceInfoResult.class, this);
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        DeviceInfoResult info = (DeviceInfoResult) result;
        if (null != mOnRequestFirstPageInfoCallback) {
            mOnRequestFirstPageInfoCallback.onRequestFirstPageInfoSuccess(info.data);
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mOnRequestFirstPageInfoCallback) {
            mOnRequestFirstPageInfoCallback.onRequestFirstPageInfoFail(code, errMsg);
        }
    }

    public interface OnRequestFirstPageInfoCallback {
        void onRequestFirstPageInfoSuccess(DeviceInfoResult mDeviceInfoResult);

        void onRequestFirstPageInfoFail(int code, String errMsg);
    }


}
