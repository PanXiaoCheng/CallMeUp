package com.mrpan.callmeup;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

import com.mrpan.callmeup.util.CollectionUtils;
import com.mrpan.callmeup.util.SharedPreferenceUtil;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;

/**
 * Created by skywish on 2015/6/23.
 */
public class CustomApplcation extends Application {

    public static CustomApplcation mInstance;
    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    @Override
    public void onCreate() {
        super.onCreate();
        //同步设置调试模式，方便开发者调试，正式发布时需注释
        BmobChat.DEBUG_MODE = true;
        mInstance = this;

    }

    private void Init() {
        if (BmobUserManager.getInstance(getApplicationContext()).getCurrentUser() != null) {
            //获取本地好友列表到内存
            //创建并打开BmobDB：默认打开以当前登录用户名为dbName的数据库
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext())
                    .getContactList());
        }
    }

    public static CustomApplcation getInstance() {
        return mInstance;
    }

    SharedPreferenceUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_SHAREDNAME";

    /**
     * 获取工具类，存储各种设置，若为空，则新建当前用户ID为名字的SharedPreferences，否则返回已存在的
     * @return
     */
    public synchronized SharedPreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = BmobUserManager.getInstance(getApplicationContext())
                    .getCurrentUserObjectId();
            String shareName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharedPreferenceUtil(this, shareName);
        }
        return mSpUtil;
    }

    /**
     * 获取内存中好友的list
     * @return
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * 设置list
     * @param contactList Map<String, BmobChatUser>
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        this.contactList = contactList;
    }

    /**
     * 退出登录，清空缓存
     */
    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();;
        setContactList(null);
    }
}
