package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;

/**
 * Created by guangbinw on 2017/3/14.
 * 删除场景
 */
public class RequestDeleteScene implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private CommentRequestCallback mCommentRequestCallback;

    public static RequestDeleteScene getInstance() {
        return new RequestDeleteScene();
    }

    /**
     * 删除场景
     */
    public void deleteScene(Context context, int sceneId, CommentRequestCallback mCommentRequestCallback) {
        this.context = context;
        this.mCommentRequestCallback = mCommentRequestCallback;
        if (UserUtils.isLogin(context))
            HttpUtils.getInstance().deleteRequestInfo(NetConfig.URL_DELETE_SCENE + sceneId + "?access_token=" + UserUtils.getAccessToken(context),
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
