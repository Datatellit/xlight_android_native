package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by luomengxin on 2017/5/11.
 * 主页上展示的温度、设备列表等信息
 */

public class Sensorsdata implements Serializable {
    /**
     * 温度
     */
    public float DHTt;
    /**
     * 湿度
     */
    public float DHTh;
    /**
     * 亮度
     */
    public int ALS;
    /**
     * 动作
     */
    public int PIR;
    /**
     * 烟雾
     */
    public int smk;
    /**
     * 气体
     */
    public int gas;
    /**
     * 噪音
     */
    public int MIC;
    /**
     * PM25
     */
    public int PM25;

    @Override
    public String toString() {
        return "Sensorsdata{" +
                "DHTt=" + DHTt +
                ", DHTh=" + DHTh +
                ", ALS=" + ALS +
                ", PIR=" + PIR +
                ", smk=" + smk +
                ", gas=" + gas +
                ", MIC=" + MIC +
                ", PM25=" + PM25 +
                '}';
    }
}
