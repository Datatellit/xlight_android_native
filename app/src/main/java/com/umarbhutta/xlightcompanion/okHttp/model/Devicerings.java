package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class Devicerings implements Serializable{
    public int id;
    public int devicenodeId;
    public int deviceId;
    public int nodeno;
    public String ringname;
    public int ringno;
    public String ison;
    public int brightness;
    public int R;
    public int G;
    public int B;
    public int W;
    public String createdAt;
    public String updatedAt;

    @Override
    public String toString() {
        return "Devicerings{" +
                "id=" + id +
                ", devicenodeId=" + devicenodeId +
                ", deviceId=" + deviceId +
                ", nodeno=" + nodeno +
                ", ringname='" + ringname + '\'' +
                ", ringno=" + ringno +
                ", ison='" + ison + '\'' +
                ", brightness=" + brightness +
                ", R=" + R +
                ", G=" + G +
                ", B=" + B +
                ", W=" + W +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
