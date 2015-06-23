package com.mrpan.callmeup.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrpan.callmeup.R;
import com.mrpan.callmeup.util.UtilLogin;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mr.wang on 2015/6/23.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private Button btLogin;
    private Button btShare;
    private TextView nicknameTextView;
    private TextView openidTextView;
    private ImageView userlogo;

    private Tencent mTencent;
    public static String mAppid = "1104720566";
    public static String openidString;
    public static String nicknameString;
    public static String TAG="MainActivity";
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        nicknameTextView = (TextView)findViewById(R.id.niackname);
        openidTextView = (TextView)findViewById(R.id.openid);
        userlogo = (ImageView)findViewById(R.id.imageView);
        btLogin = (Button)findViewById(R.id.login);
        btLogin.setOnClickListener(this);
        btShare = (Button)findViewById(R.id.share);
        btShare.setOnClickListener(this);
    }

    public void Login(){
        mTencent = Tencent.createInstance(mAppid,this.getApplicationContext());
        mTencent.login(this,"all", new BaseUiListener());
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
            try {
                //获得的数据是JSON格式的，获得你想获得的内容
                //如果你不知道你能获得什么，看一下下面的LOG
                Log.e(TAG, "-------------" + response.toString());
                openidString = ((JSONObject) response).getString("openid");
                openidTextView.setText(openidString);
                Log.e(TAG, "-------------"+openidString);
                //access_token= ((JSONObject) response).getString("access_token");				//expires_in = ((JSONObject) response).getString("expires_in");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            QQToken qqToken = mTencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(), qqToken);
            info.getUserInfo(new IUiListener() {

                public void onComplete(final Object response) {
                    // TODO Auto-generated method stub
                    Log.e(TAG, "---------------111111");
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    Log.e(TAG, "-----111---"+response.toString());
                    /**由于图片需要下载所以这里使用了线程，如果是想获得其他文字信息直接
                     * 在mHandler里进行操作
                     *
                     */
                    new Thread(){

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            JSONObject json = (JSONObject)response;
                            try {
                                bitmap = UtilLogin.getbitmap(json.getString("figureurl_qq_2"));
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.obj = bitmap;
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        }
                    }.start();
                }
                public void onCancel() {
                    Log.e(TAG, "--------------111112");
                    // TODO Auto-generated method stub
                }
                public void onError(UiError arg0) {
                    // TODO Auto-generated method stub
                    Log.e(TAG, "-111113"+":"+arg0);
                }

            });

        }

        @Override
        public void onError(UiError arg0) {
            // TODO Auto-generated method stub

        }

    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                JSONObject response = (JSONObject) msg.obj;
                if (response.has("nickname")) {
                    try {
                        nicknameString = response.getString("nickname");

                        nicknameTextView.setText(nicknameString);
                        Log.e(TAG, "--"+nicknameString);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }else if(msg.what == 1){
                Bitmap bitmap = (Bitmap)msg.obj;
                userlogo.setImageBitmap(bitmap);

            }
        }

    };

    public void Share(){
        if (mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    Toast.makeText(getApplication(),"share success",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onError(UiError uiError) {

                }
                @Override
                public void onCancel() {

                }
            };
            Bundle params = new Bundle();

            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, "SHARE APP");
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "The app is callmeup");
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "CallMeUp");

            mTencent.shareToQQ(LoginActivity.this, params, listener);
        }
        else{
            Toast.makeText(this,"Please login first",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                Login();
                break;
            case R.id.share:
                Share();
            default:
                break;
        }
    }
}
