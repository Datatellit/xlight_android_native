package com.umarbhutta.xlightcompanion.okHttp.model;

/**
 * Created by guangbinw on 2017/3/17.
 */

public class CreateRuleResult {
    public int code;
    public String msg;

    public CreateRuleResult data;

    public int id;
    public String updatedAt;
    public String createdAt;

    @Override
    public String toString() {
        return "CreateRuleResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", id=" + id +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
