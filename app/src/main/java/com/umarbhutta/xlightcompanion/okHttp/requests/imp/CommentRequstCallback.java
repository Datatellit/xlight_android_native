package com.umarbhutta.xlightcompanion.okHttp.requests.imp;

/**
 * Created by guangbinw on 2017/3/14.
 */
public interface CommentRequstCallback {
    void onCommentRequstCallbackSuccess();

    void onCommentRequstCallbackFail(int code, String errMsg);
}
