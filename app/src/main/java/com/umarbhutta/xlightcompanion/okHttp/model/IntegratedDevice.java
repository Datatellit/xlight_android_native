package com.umarbhutta.xlightcompanion.okHttp.model;

import java.util.Date;

/**
 * Created by guangbinw on 2017/3/17.
 */

public class IntegratedDevice {
    public int id;
    public int deviceId;
    public int state;
    public int type;
    public String name;
    public String text;
    public Date createdAt;

    @Override
    public String toString() {
        return "IntegratedDevice{" +
                "id=" + id + ',' +
                "deviceId=" + deviceId + ',' +
                "state=" + state + ',' +
                "type=" + type + ',' +
                "name='" + name + '\'' + ',' +
                "text='" + text + '\'' + ',' +
                "createdAt='" + createdAt.toString() + '\'' +
                '}';
    }
}
