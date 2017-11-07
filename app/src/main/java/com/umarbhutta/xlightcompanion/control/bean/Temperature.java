package com.umarbhutta.xlightcompanion.control.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class Temperature implements Serializable {
    public String name;
    public String value;

    @Override
    public String toString() {
        return "Temperature{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
