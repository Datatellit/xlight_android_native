package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/17.
 * 亮度2、活动3、声音4、温度5、离家6、回家7、气体8
 */

public class ConditionOld implements Serializable {
    public int id;
    public String ruleconditionname;
    public int devicenodeId;
    public int sensorId;
    public String attribute;
    public String operator;
    public String rightValue;
    public int status;
    public int conditionType;
    public String temAbove;
    /**
     * 亮度2、活动3、声音4、温度5、离家6、回家7、气体8
     */
    public int ruleconditiontype;

    @Override
    public String toString() {
        return "Condition{" +
                "id=" + id +
                ",ruleconditionname='" + ruleconditionname + '\'' +
                ", devicenodeId=" + devicenodeId +
                ", attribute='" + attribute + '\'' +
                ", operator='" + operator + '\'' +
                ", rightValue='" + rightValue + '\'' +
                ", status=" + status +
                ", conditionType=" + conditionType +
                ", temAbove='" + temAbove + '\'' +
                ", ruleconditiontype=" + ruleconditiontype +
                '}';
    }
}
