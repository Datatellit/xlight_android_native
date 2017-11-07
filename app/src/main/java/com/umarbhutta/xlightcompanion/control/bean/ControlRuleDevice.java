package com.umarbhutta.xlightcompanion.control.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/2.
 * 创建规则设备控制页面的对象。
 * 灯具控制1、场景控制2（实际上场景控制使用的Actioncmd）
 */

public class ControlRuleDevice implements Serializable {
    public int id;
    public String devicename;
    public String roomName;
    public String scenarioname;
    public int brightness;
    public int cct;
    public int type;

    public int userId;

    public String statues;//状态 开，关

    /**
     * 1：灯具控制；2：场景控制
     */
    public int actiontype;

}
