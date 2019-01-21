package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.IntegratedInfoResult;
import com.umarbhutta.xlightcompanion.okHttp.model.Rows;

import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 * 首页基本信息接口
 */
public class RequestIntegratedInfo implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private OnRequestIntegratedInfoCallback mOnRequestIntegratedInfoCallback;

    public static RequestIntegratedInfo getInstance(Context context) {
        return new RequestIntegratedInfo(context);
    }

    private RequestIntegratedInfo(Context context) {
        this.context = context;
    }

    /**
     * 获取信息
     */
    public void getIntegratedDevices(OnRequestIntegratedInfoCallback mOnRequestIntegratedInfoCallback) {
        this.mOnRequestIntegratedInfoCallback = mOnRequestIntegratedInfoCallback;
        // if (UserUtils.isLogin(context)) {
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_GET_INTEGRATED + UserUtils.getAccessToken(context),
                IntegratedInfoResult.class, this);
        // }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        IntegratedInfoResult info = (IntegratedInfoResult) result;

        if (info.code == 1) {
            if (null != mOnRequestIntegratedInfoCallback) {
                mOnRequestIntegratedInfoCallback.onRequestIntegratedInfoSuccess(info.data);
            }
        } else {
            if (null != mOnRequestIntegratedInfoCallback) {
                mOnRequestIntegratedInfoCallback.onRequestIntegratedInfoFail(info.code, info.msg);
            }
        }

    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mOnRequestIntegratedInfoCallback) {
            mOnRequestIntegratedInfoCallback.onRequestIntegratedInfoFail(NetConfig.ERROR_NET_ERROT, context.getString(R.string.net_error));
        }
    }

    public interface OnRequestIntegratedInfoCallback {
        void onRequestIntegratedInfoSuccess(List<Rows> devices);

        void onRequestIntegratedInfoFail(int code, String errMsg);
    }

}
