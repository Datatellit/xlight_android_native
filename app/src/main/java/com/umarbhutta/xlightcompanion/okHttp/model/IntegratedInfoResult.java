package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 * 设备信息
 */

public class IntegratedInfoResult implements Serializable {

    public int code;
    public String msg;
    public List<Rows> data;

    @Override
    public String toString() {
        return "IntegratedInfoResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
