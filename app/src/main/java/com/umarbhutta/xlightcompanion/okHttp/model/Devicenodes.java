package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class Devicenodes implements Serializable {
    public int id;
    public int deviceId;
    public String devicenodename;
    public int nodeno;
    public int ison;
    public int brightness;
    public int cct;
    public String scenarioId;
    public String createdAt;
    public String updatedAt;
    public int devicetype; //1,sunny 2,rainbow 4,mirage
    public int devicenodetype;
    //    public List<String> sensorsdata;
    public List<Devicerings> devicerings;
    public List<Ruleconditions> ruleconditions;
    public int[] color;
    public int isShare = 0;
    public String coreid;

    /**
     * 是否为主设备，0否，1是
     */
    public int maindevice;

    @Override
    public String toString() {
        return "Devicenodes{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", devicenodename='" + devicenodename + '\'' +
                ", nodeno=" + nodeno +
                ", ison=" + ison +
                ", brightness=" + brightness +
                ", cct=" + cct +
                ", scenarioId='" + scenarioId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", devicenodetype='" + devicenodetype + '\'' +
                ", devicetype='" + devicetype + '\'' +
                ", devicerings=" + devicerings +
                ", ruleconditions=" + ruleconditions +
                ", coreid='" + coreid + '\'' +
                '}';
    }
}
