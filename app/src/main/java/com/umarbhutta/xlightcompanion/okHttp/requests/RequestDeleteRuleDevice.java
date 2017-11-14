package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequstCallback;

/**
 * Created by guangbinw on 2017/3/14.
 * 删除规则
 */
public class RequestDeleteRuleDevice implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private CommentRequstCallback mCommentRequstCallback;

    public static RequestDeleteRuleDevice getInstance() {
        return new RequestDeleteRuleDevice();
    }

    /**
     * 删除规则
     */
    public void deleteRule(Context context, String deviceId, CommentRequstCallback mCommentRequstCallback) {
        this.context = context;
        this.mCommentRequstCallback = mCommentRequstCallback;
        if (UserUtils.isLogin(context))
            HttpUtils.getInstance().deleteRequestInfo(NetConfig.URL_DELETE_RULE + deviceId + "?access_token=" + UserUtils.getAccessToken(context),
                    null, null, this);
    }


    @Override
    public void onHttpRequestSuccess(Object result) {
        if (null != mCommentRequstCallback) {
            mCommentRequstCallback.onCommentRequstCallbackSuccess();
        }
    }

    @Override
    public void onHttpRequestFail(int code, String errMsg) {
        if (null != mCommentRequstCallback) {
            if (code == 0) {
                mCommentRequstCallback.onCommentRequstCallbackSuccess();
            }
            mCommentRequstCallback.onCommentRequstCallbackFail(code, errMsg);
        }
    }


}
