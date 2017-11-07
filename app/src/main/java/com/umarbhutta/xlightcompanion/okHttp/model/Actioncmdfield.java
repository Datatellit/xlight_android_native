package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/17.
 */

public class Actioncmdfield implements Serializable {
    public int id;
    public int ruleactioncmdId;
    public String cmd;
    public String paralist;
    public String sensorId;
    public String sensortype;


    @Override
    public String toString() {
        return "Actioncmdfield{" +
                "id=" + id +
                ", ruleactioncmdId=" + ruleactioncmdId +
                ", cmd='" + cmd + '\'' +
                ", paralist='" + paralist + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", sensortype='" + sensortype + '\'' +
                '}';
    }
}
