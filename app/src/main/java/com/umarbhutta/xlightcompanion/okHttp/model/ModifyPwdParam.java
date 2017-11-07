package com.umarbhutta.xlightcompanion.okHttp.model;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class ModifyPwdParam {

    public ModifyPwdParam(String oldpassword, String newpassword) {
        this.oldpassword = oldpassword;
        this.newpassword = newpassword;
    }

    public String oldpassword;
    public String newpassword;

    @Override
    public String toString() {
        return "ModifyPwdParam{" +
                "oldpassword='" + oldpassword + '\'' +
                ", newpassword='" + newpassword + '\'' +
                '}';
    }
}
