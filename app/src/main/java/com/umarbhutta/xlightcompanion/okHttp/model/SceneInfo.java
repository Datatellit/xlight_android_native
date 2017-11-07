package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/15.
 * 场景信息
 */

public class SceneInfo implements Serializable{
    public int code;
    public String msg;
    public List<Rows> data;

    @Override
    public String toString() {
        return "SceneInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
