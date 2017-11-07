package com.umarbhutta.xlightcompanion.okHttp.model;

import java.util.Date;

/**
 * Created by guangbinw on 2017/3/15.
 * 添加设备接口返回
 */

public class AddDeviceResult {
    public int code;
    public String msg;
    public Device data;

    @Override
    public String toString() {
        return "AddDeviceResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}

class Device {
    public int id;
    public int deviceId;
    public String devicenodename;
    public int devicetype;
    public int nodeno;
    public int ison;
    public int brightness;
    public int cct;
    public int sId;
    public Date updateAt;
    public Date createdAt;
}
