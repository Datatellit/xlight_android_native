package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;

/**
 * Created by guangbinw on 2017/3/13.
 */

public class DeviceUser implements Serializable{
    public int id;
    public String username;
    public String password;
    public String salt;
    public String firstname;
    public String lastname;
    public String email;
    public String image;
    public int channelcode;
    public String createdAt;
    public String updatedAt;
    public int usergroupId;
    public int sex;
    public String nickname;
    public String verificationcode;
    public String expirationtime;

    @Override
    public String toString() {
        return "DeviceUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", channelcode=" + channelcode +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", usergroupId=" + usergroupId +
                ", sex=" + sex +
                ", nickname='" + nickname + '\'' +
                ", verificationcode='" + verificationcode + '\'' +
                ", expirationtime='" + expirationtime + '\'' +
                '}';
    }
}
