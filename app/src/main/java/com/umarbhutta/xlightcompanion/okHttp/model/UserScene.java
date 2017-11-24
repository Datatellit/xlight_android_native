package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 75932 on 2017/11/17.
 */


public class UserScene implements Serializable {
    public int id;
    public int userId;
    public int sceneId;
    public Date createdAt;
    public Date updatedAt;

    public String toString() {
        return "{'id':" + this.id + "," +
                "'userId':" + this.userId + "," +
                "'sceneId':" + this.sceneId + "}";
    }
}