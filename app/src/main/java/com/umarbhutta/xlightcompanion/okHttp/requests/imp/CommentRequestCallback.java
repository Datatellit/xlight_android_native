package com.umarbhutta.xlightcompanion.okHttp.requests.imp;

/**
 * Created by guangbinw on 2017/3/14.
 */
public interface CommentRequestCallback {
    void onCommentRequestCallbackSuccess();

    void onCommentRequestCallbackFail(int code, String errMsg);
}
