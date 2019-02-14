package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/13.
 * 服务器返回的执行条件
 */

public class Ruleconditions implements Serializable {
    public int id;
    public String ruleconditionname;
    public int ruleId;
    public int devicenodeId;
    public int sensorId;
    public String attribute;
    public String operator;
    public String rightValue;
    public String starttime;
    public String endtime;
    public String weekdays;
    public int hour;
    public int minute;
    public int isrepeat;
    public int status;
    public String createdAt;
    public String updatedAt;
    /**
     * 1:定时；2：亮度；3：活动；4：声音；5：温度；6：离家；7：回家；8：气体
     */
    public int ruleconditiontype;

    @Override
    public String toString() {
        return "Ruleconditions{" +
                "id=" + id +
                ", ruleconditionname='" + ruleconditionname + '\'' +
                ", ruleId=" + ruleId +
                ", devicenodeId=" + devicenodeId +
                ", sensorId='" + sensorId +
                ", attribute='" + attribute + '\'' +
                ", operator='" + operator + '\'' +
                ", rightValue='" + rightValue + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", weekdays='" + weekdays + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                ", isrepeat=" + isrepeat +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
