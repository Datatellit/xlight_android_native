package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class Rows implements Serializable {
    public int id;
    public String devicename;
    public String scenarioname;
    public int brightness;
    public int cct;
    public int type;
    public String coreid;
    public int areaId;


    public int userId;
    public String iphoneidentify;
    public String androididentify;
    public int isShare = 0;
    /**
     * 是否为主设备，0否，1是
     */
    public int maindevice;
    /**
     * 设备的启用禁用状态，0禁用，1启用
     */
    public int status;
    /**
     * 设备开关状态，0关，1开
     */
    public int ison;
    public String createdAt;
    public String updatedAt;
    public int devicegroupId;

    public Devicegroup devicegroup;
    public DeviceUser user;
    public List<Devicenodes> devicenodes;
    public List<Scenarionodes> scenarionodes;
    public ShareDevice sharedevice;
    /**
     * 主页上展示的温度、设备列表等信息
     */
    public Sensorsdata sensorsdata;

    @Override
    public String toString() {
        return "Rows{" +
                "id=" + id +
                ", devicename='" + devicename + '\'' +
                ", scenarioname='" + scenarioname + '\'' +
                ", brightness=" + brightness +
                ", cct=" + cct +
                ", type=" + type +
                ", coreid='" + coreid + '\'' +
                ", areaId=" + areaId +
                ", userId=" + userId +
                ", iphoneidentify='" + iphoneidentify + '\'' +
                ", androididentify='" + androididentify + '\'' +
                ", maindevice=" + maindevice +
                ", status=" + status +
                ", ison=" + ison +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", devicegroupId=" + devicegroupId +
                ", devicegroup=" + devicegroup +
                ", user=" + user +
                ", devicenodes=" + devicenodes +
                ", scenarionodes=" + scenarionodes +
                ", sensorsdata=" + sensorsdata +
                '}';
    }
}
