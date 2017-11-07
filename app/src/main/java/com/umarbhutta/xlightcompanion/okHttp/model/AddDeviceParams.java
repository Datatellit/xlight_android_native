package com.umarbhutta.xlightcompanion.okHttp.model;

/**
 * Created by 75932 on 2017/11/3.
 */

public class AddDeviceParams {
    public String coreid;
    public String devicename;
    public int userId;
    public String androididentify;
    public int maindevice = 1;
    public int devicetype = 2;

    public AddDeviceParams(String coreID, String deviceName, int userId, String androididentify, int maindevice, int devicetype) {
        this.coreid = coreID;
        this.devicename = deviceName;
        this.userId = userId;
        this.androididentify = androididentify;
        this.maindevice = maindevice;
        this.devicetype = devicetype;
    }
}
