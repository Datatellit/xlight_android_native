package io.particle.android.sdk.devicesetup.model;

public class AddDeviceParams {
    public String coreid;
    public String devicename;
    public int userId;
    public String androididentify;
    public int maindevice = 1;
    public int devicetype = 2;
    public boolean isClaim = false;

    public AddDeviceParams(String coreID, String deviceName, int userId, String androididentify, int maindevice, int devicetype,boolean isClaim) {
        this.coreid = coreID;
        this.devicename = deviceName;
        this.userId = userId;
        this.androididentify = androididentify;
        this.maindevice = maindevice;
        this.devicetype = devicetype;
        this.isClaim = isClaim;
    }

    public AddDeviceParams(String coreID, String deviceName, String androididentify, int maindevice, int devicetype,boolean isClaim) {
        this.coreid = coreID;
        this.devicename = deviceName;
        this.androididentify = androididentify;
        this.maindevice = maindevice;
        this.devicetype = devicetype;
        this.isClaim = isClaim;
    }
}