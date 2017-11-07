package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/17.
 * Emial通知、APP通知
 */

public class Actionnotify implements Serializable {
    public String msisdn;
    public String emailaddress;
    public String content;
    public String subject;
    public List<Alarm> alarm;
    public int actionnotifyType;
    public String name;
    /**
     * 1：email通知;2:APP通知
     */
    public int actiontype;

    @Override
    public String toString() {
        return "Actionnotify{" +
                "msisdn='" + msisdn + '\'' +
                ", content='" + content + '\'' +
                ", alarm=" + alarm +
                '}';
    }
}
