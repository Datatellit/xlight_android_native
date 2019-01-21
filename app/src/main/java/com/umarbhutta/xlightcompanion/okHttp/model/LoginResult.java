package com.umarbhutta.xlightcompanion.okHttp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by guangbinw on 2017/3/12.
 */

public class LoginResult implements Serializable {
    public List<LoginResult> data;

    public int code;
    public String msg;

    public int id;
    public String username;
    public String password;
    public String salt;
    public String firstname;
    public String lastname;
    /**
     * 0代表女  1代表男   2代表保密
     */
    public int sex = 2;
    public String nickname;
    public String email;
    public String image;
    public String channelcode;
    public String verificationcode;
    public String expirationtime;
    public String createdAt;
    public String updatedAt;
    public String usergroupId;
    public String access_token;
    public Date expires;


    public List<LoginResult> getData() {
        return data;
    }

    public void setData(List<LoginResult> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public Date getExpires() {
        return expires;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChannelcode() {
        return channelcode;
    }

    public void setChannelcode(String channelcode) {
        this.channelcode = channelcode;
    }

    public String getVerificationcode() {
        return verificationcode;
    }

    public void setVerificationcode(String verificationcode) {
        this.verificationcode = verificationcode;
    }

    public String getExpirationtime() {
        return expirationtime;
    }

    public void setExpirationtime(String expirationtime) {
        this.expirationtime = expirationtime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsergroupId() {
        return usergroupId;
    }

    public void setUsergroupId(String usergroupId) {
        this.usergroupId = usergroupId;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "code=" + code +
                "msg=" + msg +
                "data=" + data +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", sex='" + sex + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", channelcode='" + channelcode + '\'' +
                ", verificationcode='" + verificationcode + '\'' +
                ", expirationtime='" + expirationtime + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", usergroupId='" + usergroupId + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires='" + expires + '\'' +
                '}';
    }
}
