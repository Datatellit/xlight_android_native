package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/15.
 */

public class Scenarionodes implements Serializable{
    public int id;
    public int scenarioId;
    public int brightness;
    public int R;
    public int G;
    public int B;
    public String W;
    public int cct;
    public String createdAt;
    public String updatedAt;
    public String color;


    /**
     * 已用，勿删
     * @param brightness
     * @param r
     * @param g
     * @param b
     * @param cct
     * @param color
     */
    public Scenarionodes(int brightness, int r, int g, int b, int cct, String color) {
        this.brightness = brightness;
        R = r;
        G = g;
        B = b;
        this.cct = cct;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Scenarionodes{" +
                "id=" + id +
                ", scenarioId=" + scenarioId +
                ", brightness=" + brightness +
                ", R=" + R +
                ", G=" + G +
                ", B=" + B +
                ", W='" + W + '\'' +
                ", cct=" + cct +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
