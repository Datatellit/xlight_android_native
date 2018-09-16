package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

public class DeviceState implements Serializable {
    public List<Light> light;
    public List<Sensorsdata> sensor;
    public String coreid;
    public int id;
    public boolean connected;
    public String deviceId;
    public String name;
    public String userId;
}
