package com.umarbhutta.xlightcompanion.okHttp.model;

import java.util.Date;
import java.util.List;

/**
 * Created by 75932 on 2017/11/23.
 */

public class ShareResult {
    public int code;
    public String msg;
    public List<ShareResult> data;

    public int id;
    public int userId;
    public int deviceId;
    public int type;
    public String content;
    public int state;
    public Date expirationtime;
    public Date createdAt;
    public ShareDevice sharedevice;
    public LoginResult user;
    public Rows device;
}
