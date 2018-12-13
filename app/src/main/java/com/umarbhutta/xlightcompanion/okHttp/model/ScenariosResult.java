package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by 75932 on 2017/11/17.
 */

public class ScenariosResult implements Serializable {
    public int id;
    public String scenarioname;
    public int userId;
    public String image;
    public int cct;
    public int brightness;
    public int rgb;
    public int filter;
    public int sort;
    public Date createdAt;
    public Date updatedAt;
    public int type;

    public String toString() {
        return "{'id':" + this.id + "," +
                "'name':'" + this.scenarioname + "'," +
                "'userId':'" + this.userId + "'," +
                "'cct':'" + this.cct + "'," +
                "'brightness':'" + this.brightness + "'," +
                "'rgb':'" + this.rgb + "'," +
                "'filter':'" + this.filter + "'," +
                "'sort':'" + this.sort + "'," +
                "'image':" + this.image + "}";
    }
}
