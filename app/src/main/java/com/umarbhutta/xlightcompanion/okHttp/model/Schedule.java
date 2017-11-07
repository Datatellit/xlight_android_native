package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/17.
 * 定时、
 */

public class Schedule implements Serializable {
    public String ruleconditionname;
    public int devicenodeId;
    public String weekdays;
    public int hour;
    public int minute;
    public int status;
    public int isrepeat;//0代表不重复，1代表重复
    public String scheduleName;
    /**
     * 条件类型：0:混合条件；1:定时；2：亮度；3：活动；4：声音；5：温度；6：离家；7：回家；8：气体
     */
    public int ruleconditiontype;

    @Override
    public String toString() {
        return "Schedule{" +
                "ruleconditionname='" + ruleconditionname + '\'' +
                ", devicenodeId=" + devicenodeId +
                ", weekdays='" + weekdays + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                ", status=" + status +
                ", isrepeat=" + isrepeat +
                ", scheduleName='" + scheduleName + '\'' +
                ", ruleconditiontype=" + ruleconditiontype +
                '}';
    }
}
