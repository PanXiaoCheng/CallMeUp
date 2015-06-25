package com.mrpan.callmeup.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mrpan.callmeup.R;
import com.mrpan.callmeup.bean.User;
import com.mrpan.callmeup.config.BmobConstants;
import com.mrpan.callmeup.util.CommonUtils;

import cn.bmob.im.config.BmobConstant;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by skywish on 2015/6/24.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_register, tv_forget;
    private Button btn_login;
    private EditText et_account, et_password;

    String username, password, encodedPass;
    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_account = (EditText) findViewById(R.id.login_edit_account);
        et_password = (EditText) findViewById(R.id.login_edit_password);

        btn_login = (Button) findViewById(R.id.login_button_login);
        btn_login.setOnClickListener(this);
        tv_register = (TextView) findViewById(R.id.login_text_register);
        tv_register.setOnClickListener(this);
        tv_forget = (TextView) findViewById(R.id.login_text_forget);
        tv_forget.setOnClickListener(this);

        //注册退出广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button_login:
                login();
                break;
            case R.id.login_text_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.login_text_forget:
                //TODO
                break;
            default:
                break;
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null &&
                    BmobConstants.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    }

    public void login() {
        username = et_account.getText().toString();
        password = et_password.getText().toString();
        encodedPass = CommonUtils.encodePsd(password);

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            Toast.makeText(this, "请联网登陆！", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登陆……");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPass);
        userManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {
                updateUserInfos();
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
