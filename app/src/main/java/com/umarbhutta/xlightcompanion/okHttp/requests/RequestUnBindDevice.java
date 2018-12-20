package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;

/**
 * Created by guangbinw on 2017/3/14.
 * 解绑设备接口
 */
public class RequestUnBindDevice implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private CommentRequestCallback mCommentRequestCallback;

    public static RequestUnBindDevice getInstance() {
        return new RequestUnBindDevice();
    }

    /**
     * 解绑设备
     */
    public void unBindDevice(Context context, String deviceId, CommentRequestCallback mCommentRequestCallback) {
        this.context = context;
        this.mCommentRequestCallback = mCommentRequestCallback;
        if (UserUtils.isLogin(context))
            HttpUtils.getInstance().putRequestInfo(NetConfig.URL_UNBIND_DEVICE + deviceId + "/unbind?access_token=" + UserUtils.getAccessToken(context),
                    null, null, this);
    }

    /**
     * 解绑controller
     */
    public void unBindController(Context context, String deviceId, CommentRequestCallback mCommentRequestCallback) {
        this.context = context;
        this.mCommentRequestCallback = mCommentRequestCallback;
        HttpUtils.getInstance().putRequestInfo(NetConfig.URL_UNBIND_CONTROLLER + deviceId + "/unbind?access_token=" + UserUtils.getAccessToken(context),
                null, null, this);
    }


    @Override
    public void onHttpRequestSuccess(Object result) {
        if (null != mCommentRequestCallback) {
            mCommentRequestCallback.onCommentRequestCallbackSuccess();
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mCommentRequestCallback) {
            mCommentRequestCallback.onCommentRequestCallbackFail(code, errMsg);
        }
    }


}
