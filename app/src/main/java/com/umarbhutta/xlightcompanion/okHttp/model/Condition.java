package com.umarbhutta.xlightcompanion.okHttp.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/17.
 * 亮度2、活动3、声音4、温度5、离家6、回家7、气体8
 */

public class Condition implements Serializable {
    public String type;
    public String name;
    public String remark;
    public String state;
    public JSONObject object;

    @Override
    public String toString() {
        return "Condition{" +
                "type='" + type + '\'' +
                ", name=" + name +
                ", remark='" + remark + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
