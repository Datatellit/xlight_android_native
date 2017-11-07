package com.umarbhutta.xlightcompanion.control.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class Activities implements Serializable {
    public String name;
    public int value;

    @Override
    public String toString() {
        return "Activities{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
