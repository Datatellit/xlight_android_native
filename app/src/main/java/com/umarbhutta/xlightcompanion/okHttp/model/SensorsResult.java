package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 75932 on 2017/10/30.
 */

public class SensorsResult implements Serializable {
    public int code;
    public String msg;
    public List<Sensorsdata> data;
}
