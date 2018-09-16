package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luomengxin on 2017/3/18.
 * 添加场景接口参数
 */

public class Light implements Serializable{
    public int nd;
    public int sid;
    public int State;
    public int BR;
    public int CCT;
    public int filter;
    public int W;
    public int R;
    public int G;
    public int B;
    @Override
    public String toString() {
        return "Sensorsdata{" +
                "nd=" + nd +
                ", State=" + State +
                ", BR=" + BR +
                ", CCT=" + CCT +
                ", filter=" + filter +
                ", W=" + W +
                ", R=" + R +
                ", G=" + G +
                ", B=" + B +
                '}';
    }
}
