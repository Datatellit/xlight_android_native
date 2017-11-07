package com.umarbhutta.xlightcompanion.control.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class Ruleconditions implements Serializable{

    public List<Ruleconditions> data;

    public List<Activities> activities;//活动
    public List<Voice> voice;//声音
    public List<LeaveHome> leavehome;//离家
    public List<GoHome> gohome;//回家
    public List<Gas> gas;//气体
    public List<Temperature> temperature;//温度

    public List<Ruleconditions> getData() {
        return data;
    }

    public void setData(List<Ruleconditions> data) {
        this.data = data;
    }

    public List<Activities> getActivities() {
        return activities;
    }

    public void setActivities(List<Activities> activities) {
        this.activities = activities;
    }

    public List<Voice> getVoice() {
        return voice;
    }

    public void setVoice(List<Voice> voice) {
        this.voice = voice;
    }

    public List<LeaveHome> getLeavehome() {
        return leavehome;
    }

    public void setLeavehome(List<LeaveHome> leavehome) {
        this.leavehome = leavehome;
    }

    public List<GoHome> getGohome() {
        return gohome;
    }

    public void setGohome(List<GoHome> gohome) {
        this.gohome = gohome;
    }

    public List<Gas> getGas() {
        return gas;
    }

    public void setGas(List<Gas> gas) {
        this.gas = gas;
    }

    public List<Temperature> getTemperature() {
        return temperature;
    }

    public void setTemperature(List<Temperature> temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "Ruleconditions{" +
                "data=" + data +
                ", activities=" + activities +
                ", voice=" + voice +
                ", leavehome=" + leavehome +
                ", gohome=" + gohome +
                ", gas=" + gas +
                ", temperature=" + temperature +
                '}';
    }
}
