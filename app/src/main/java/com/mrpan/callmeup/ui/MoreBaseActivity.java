package com.mrpan.callmeup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

/**
 * Created by skywish on 2015/6/25.
 */
public class MoreBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    /**
     * 检测CurrentUser是否存在，不存在则已在其他设备上登录,需重新登陆
     */
    public void checkLogin() {
        BmobUserManager userManager = BmobUserManager.getInstance(this);
        if (userManager.getCurrentUser() == null) {
            Toast.makeText(this, "当前账号已在其他设备上登陆", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
