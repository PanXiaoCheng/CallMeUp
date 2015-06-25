package com.mrpan.callmeup.ui;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.mrpan.callmeup.CustomApplcation;
import com.mrpan.callmeup.util.CollectionUtils;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by skywish on 2015/6/23.
 */
public class BaseActivity extends FragmentActivity {

    BmobUserManager userManager;
    BmobChatManager chatManager;
    CustomApplcation mApplcation;

    protected int mScreenWidth;
    protected int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = BmobUserManager.getInstance(this);
        chatManager = BmobChatManager.getInstance(this);
        mApplcation = CustomApplcation.getInstance();

        //DisplayMetircs 类可以很方便的获取分辨率。
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    /**
     * 获取好友列表并保存在application中
     */
    public void updateUserInfos() {
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onSuccess(List<BmobChatUser> list) {
                CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(list));
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
