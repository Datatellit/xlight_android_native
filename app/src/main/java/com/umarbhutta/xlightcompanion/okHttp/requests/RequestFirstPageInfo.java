package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.DeviceInfoResult;

/**
 * Created by guangbinw on 2017/3/13.
 * 首页基本信息接口
 */
public class RequestFirstPageInfo implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private OnRequestFirstPageInfoCallback mOnRequestFirstPageInfoCallback;

    public static RequestFirstPageInfo getInstance(Context context) {
        return new RequestFirstPageInfo(context);
    }

    private RequestFirstPageInfo(Context context) {
        this.context = context;
    }

    /**
     * 获取信息
     */
    public void getBaseInfo(OnRequestFirstPageInfoCallback mOnRequestFirstPageInfoCallback) {
        this.mOnRequestFirstPageInfoCallback = mOnRequestFirstPageInfoCallback;
        // if (UserUtils.isLogin(context)) {
        HttpUtils.getInstance().getRequestInfo(NetConfig.URL_FIRST_PAGE_INFO + UserUtils.getAccessToken(context),
                DeviceInfoResult.class, this);
        // }
    }

    @Override
    public void onHttpRequestSuccess(Object result) {
        DeviceInfoResult info = (DeviceInfoResult) result;

        if (info.code == 1) {
            if (null != mOnRequestFirstPageInfoCallback) {
                mOnRequestFirstPageInfoCallback.onRequestFirstPageInfoSuccess(info.data);
            }
        } else {
            if (null != mOnRequestFirstPageInfoCallback) {
                mOnRequestFirstPageInfoCallback.onRequestFirstPageInfoFail(info.code, info.msg);
            }
        }

    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mOnRequestFirstPageInfoCallback) {
            mOnRequestFirstPageInfoCallback.onRequestFirstPageInfoFail(NetConfig.ERROR_NET_ERROT, context.getString(R.string.net_error));
        }
    }

    public interface OnRequestFirstPageInfoCallback {
        void onRequestFirstPageInfoSuccess(DeviceInfoResult mDeviceInfoResult);

        void onRequestFirstPageInfoFail(int code, String errMsg);
    }

}
