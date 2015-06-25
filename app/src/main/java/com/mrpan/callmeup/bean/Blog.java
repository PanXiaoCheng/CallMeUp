package com.mrpan.callmeup.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by skywish on 2015/6/24.
 */
public class Blog extends BmobObject {

    // 简介
    private String brief;

    private User user;

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}