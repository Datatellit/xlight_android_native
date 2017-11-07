package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 * 服务器返回的规则执行结果(灯具控制和场景)
 */

public class RuleActioncmd implements Serializable {
    public int id;
    public int ruleId;
    public int devicenodeId;
    public int sensorId;
    public String createdAt;
    public String updatedAt;

    /**
     * 1：灯具控制；2：场景控制
     */
    public int actiontype;

    public List<Actioncmdfield> actioncmdfields;

    @Override
    public String toString() {
        return "RuleActioncmd{" +
                "id=" + id +
                ", ruleId=" + ruleId +
                ", devicenodeId=" + devicenodeId +
                ", sensorId=" + sensorId +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
