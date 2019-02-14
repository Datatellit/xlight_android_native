package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/17.
 * 灯具控制
 */

public class Actioncmd implements Serializable {
    public int id;
    public int devicenodeId;
    public List<Actioncmdfield> actioncmdfield;
    public int actioncmdType;
    /**
     * 1：灯具控制；2：场景控制
     */
    public int actiontype;

    @Override
    public String toString() {
        return "Actioncmd{" +
                "id=" + id +
                ",devicenodeId=" + devicenodeId +
                ", actioncmdfield=" + actioncmdfield +
                ", actioncmdType=" + actioncmdType +
                '}';
    }
}
