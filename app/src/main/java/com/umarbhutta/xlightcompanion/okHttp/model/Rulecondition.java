package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/17.
 */

public class Rulecondition implements Serializable {
    public List<ConditionOld> condition;
    public List<Schedule> schedule;

    @Override
    public String toString() {
        return "Rulecondition{" +
                "condition=" + condition +
                ", schedule=" + schedule +
                '}';
    }
}
