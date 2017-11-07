package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */

public class DeviceGetRules implements Serializable {

    public int code;
    public String msg;

    public DeviceGetRules data;

    public int count;

    public List<RuleInfo> rows;

    @Override
    public String toString() {
        return "DeviceGetRules{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", count=" + count +
                ", rows=" + rows +
                '}';
    }
}
