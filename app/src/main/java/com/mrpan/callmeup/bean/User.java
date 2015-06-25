package com.mrpan.callmeup.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by skywish on 2015/6/24.
 */
public class User extends BmobChatUser {

    // 发布的博客列表
    private BmobRelation blogs;
    // 数据拼音首字母
    private String sortLetters;
    // 性别
    private Boolean sex;
    private Blog blog;
    private String qq;
    private String phone;

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
