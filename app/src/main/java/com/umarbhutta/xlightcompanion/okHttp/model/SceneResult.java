package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by 75932 on 2017/11/17.
 */

public class SceneResult implements Serializable {
    public int id;
    public String name;
    public int code;
    public String icon;
    public String cmd;
    public String remark;
    public Date createdAt;
    public Date updatedAt;
    public boolean checked = false;
    public List<UserScene> userscenes;

    public String toString() {
        return "{'id':" + this.id + "," +
                "'name':'" + this.name + "'," +
                "'cmd':'" + this.cmd + "'," +
                "'icon':" + this.icon + "}";
    }
}
