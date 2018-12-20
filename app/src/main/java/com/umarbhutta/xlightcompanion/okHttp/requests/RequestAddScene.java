package com.umarbhutta.xlightcompanion.okHttp.requests;

import android.content.Context;

import com.google.gson.Gson;
import com.umarbhutta.xlightcompanion.Tools.UserUtils;
import com.umarbhutta.xlightcompanion.okHttp.HttpUtils;
import com.umarbhutta.xlightcompanion.okHttp.NetConfig;
import com.umarbhutta.xlightcompanion.okHttp.model.EditSceneParams;
import com.umarbhutta.xlightcompanion.okHttp.requests.imp.CommentRequestCallback;

/**
 * Created by guangbinw on 2017/3/14.
 * 添加场景
 */
public class RequestAddScene implements HttpUtils.OnHttpRequestCallBack {

    private Context context;
    private CommentRequestCallback mCommentRequestCallback;

    public static RequestAddScene getInstance() {
        return new RequestAddScene();
    }

    /**
     * 添加场景
     *
     * @param context
     * @param params
     * @param mCommentRequestCallback
     */
    public void addScene(Context context, EditSceneParams params, CommentRequestCallback mCommentRequestCallback) {
        this.context = context;
        this.mCommentRequestCallback = mCommentRequestCallback;
        if (UserUtils.isLogin(context)) {

            Gson gson = new Gson();
            String paramStr = gson.toJson(params);

            HttpUtils.getInstance().postRequestInfo(NetConfig.URL_ADD_SCENE + UserUtils.getAccessToken(context),
                    paramStr, null, this);

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
