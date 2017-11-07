package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 * 设备信息
 */

public class DeviceInfoResult implements Serializable {

    public int code;
    public String msg;

    public DeviceInfoResult data;

    public int count;

    public List<Rows> rows;

    public Energysaving Energysaving;

    @Override
    public String toString() {
        return "DeviceInfoResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", count=" + count +
                ", rows=" + rows +
                '}';
    }
}
