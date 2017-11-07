package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/25.
 * 服务器返回的规则执行结果（email通知、APP通知）
 */

public class Ruleactionnotify implements Serializable {
    public int id;
    public int ruleId;
    public String msisdn;
    public String content;
    public String emailaddress;
    public String subject;
    public String createdAt;
    public String updatedAt;
    /**
     * 1：email通知;2:APP通知
     */
    public int actiontype;

    @Override
    public String toString() {
        return "Ruleactionnotify{" +
                "id=" + id +
                ", ruleId=" + ruleId +
                ", msisdn='" + msisdn + '\'' +
                ", content='" + content + '\'' +
                ", emailaddress='" + emailaddress + '\'' +
                ", subject='" + subject + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
