package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guangbinw on 2017/3/14.
 * 忘记密码--->发送验证码
 */
public class RequestSendVerifyCode implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private CommentRequestCallback mCommentRequestCallback;

    public static RequestSendVerifyCode getInstance() {
        return new RequestSendVerifyCode();
    }

    /**
     * 发送验证码
     */
    public void sendCode(Context context, String email, CommentRequestCallback mCommentRequestCallback) {
        this.context = context;
        this.mCommentRequestCallback = mCommentRequestCallback;


        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            String param = object.toString();
            HttpUtils.getInstance().putRequestInfo(NetConfig.URL_SEND_VERIFICATION_CODE,
                    param, null, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
