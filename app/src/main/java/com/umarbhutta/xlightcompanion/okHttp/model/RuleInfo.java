package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */

public class RuleInfo implements Serializable {
    public int id;
    public String rulename;
    public int relationtype;
    public int type;
    public String description;
    public int status;

    public int duration;
    public int userId;
    public String createdAt;
    public String updatedAt;

    public List<Ruleconditions> ruleconditions;

    public List<RuleActioncmd> ruleactioncmds;

    public List<Ruleactionnotify> ruleactionnotifies;

    @Override
    public String toString() {
        return "NewRuleInfo{" +
                "id=" + id +
                ", rulename='" + rulename + '\'' +
                ", relationtype=" + relationtype +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", duration=" + duration +
                ", userId=" + userId +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", ruleconditions=" + ruleconditions +
                ", ruleactioncmds=" + ruleactioncmds +
                ", ruleactionnotifies=" + ruleactionnotifies +
                '}';
    }
}
