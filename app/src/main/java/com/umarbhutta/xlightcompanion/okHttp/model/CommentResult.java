package com.umarbhutta.xlightcompanion.okHttp.model;

/**
 * Created by guangbinw on 2017/3/13.
 * 只适用于返回结果为code和msg的返回
 */

public class CommentResult {
    public int code;
    public String msg;

    @Override
    public String toString() {
        return "CommentResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
