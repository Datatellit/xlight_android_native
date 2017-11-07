package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 * 场景列表信息
 */

public class SceneListResult implements Serializable{

    public int code;
    public String msg;

    public SceneListResult data;

    public int count;

    public List<Rows> rows;

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
