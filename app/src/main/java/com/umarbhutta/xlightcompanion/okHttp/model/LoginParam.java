package com.umarbhutta.xlightcompanion.okHttp.model;

import android.content.Intent;

/**
 * Created by guangbinw on 2017/3/12.
 */

public class LoginParam {
    public String username;
    public String password;
    public String identity;
    public int merge = 0;


    public LoginParam(String username, String password, String identity) {
        this.username = username;
        this.password = password;
        this.identity = identity;
    }

    public LoginParam(String username, String password, String identity, int merge) {
        this.username = username;
        this.password = password;
        this.identity = identity;
        this.merge = merge;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identify) {
        this.identity = identify;
    }

    @Override
    public String toString() {
        return "LoginParam{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", identity='" + identity + '\'' +
                ", merge='" + merge + '\'' +
                '}';
    }
}
