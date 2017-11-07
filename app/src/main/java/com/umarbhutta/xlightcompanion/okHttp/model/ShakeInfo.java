package com.umarbhutta.xlightcompanion.okHttp.model;

import java.util.List;

/**
 * Created by alan on 2017/5/23.
 * conf服务器获取的摇一摇信息
 */

public class ShakeInfo {
    public int code;
    public String msg;
    public List<ShakeInfo> data;
    public int id;
    public int userId;
    public int deviceId;
    public String coreid;
    public int devicenodeId;
    public String devicenodename;
    //    1：切换开关；2：切换场景
    public int shakeaction;


    @Override
    public String toString() {
        return "ShakeInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", id=" + id +
                ", deviceId=" + deviceId +
                ", coreid='" + coreid + '\'' +
                ", devicenodeId=" + devicenodeId +
                ", devicenodename=" + devicenodename +
                ", shakeaction=" + shakeaction +
                '}';
    }
}
