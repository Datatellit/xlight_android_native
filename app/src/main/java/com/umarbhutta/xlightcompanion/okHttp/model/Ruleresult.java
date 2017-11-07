package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/17.
 * 创建规则的结果
 */

public class Ruleresult implements Serializable {

    public List<Actionnotify> actionnotify;
    public List<Actioncmd> actioncmd;

    @Override
    public String toString() {
        return "Ruleresult{" +
                "actionnotify=" + actionnotify +
                ", actioncmd=" + actioncmd +
                '}';
    }
}
