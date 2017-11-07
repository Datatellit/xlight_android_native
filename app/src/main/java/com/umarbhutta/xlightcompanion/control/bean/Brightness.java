package com.umarbhutta.xlightcompanion.control.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/24.
 */

public class Brightness implements Serializable{
    private String name;
    private int number;

    @Override
    public String toString() {
        return "Brightness{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
