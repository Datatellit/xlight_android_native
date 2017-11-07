package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class Devicegroup implements Serializable{
    public int id;
    public String devicegroupname;
    public String createdAt;
    public String updatedAt;

    @Override
    public String toString() {
        return "Devicegroup{" +
                "id=" + id +
                ", devicegroupname='" + devicegroupname + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
