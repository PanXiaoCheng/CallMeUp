package com.mrpan.callmeup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mrpan.callmeup.R;
import com.mrpan.callmeup.ui.MainActivity;
import com.mrpan.callmeup.ui.NewFriendActivity;
import com.mrpan.callmeup.util.CollectionUtils;
import com.mrpan.callmeup.util.CommonUtils;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by skywish on 2015/6/24.
 */
public class MyMessageReceiver extends BroadcastReceiver {

    public static ArrayList<EventListener> ehList = new ArrayList<>();

    public static final int NOTIFY_ID = 0x000;
    public static int mNewNum = 0;
    // http://docs.bmob.cn/document/im/android/cn/bmob/im/BmobUserManager.html
    BmobUserManager userManager;
    // http://docs.bmob.cn/document/im/android/cn/bmob/im/bean/BmobChatUser.html
    BmobChatUser currentUser;

    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("msg");
        BmobLog.i("收到的message = " + json);

        userManager = BmobUserManager.getInstance(context);
        // 获取当前用户
        currentUser = userManager.getCurrentUser();
        boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
        if (isNetConnected) {
            parseMessage(context, json);
        } else {
            for (int i = 0; i< ehList.size(); i++) {
                ((EventListener) ehList.get(i)).onNetChange(isNetConnected);
            }
        }
    }

    /**
     * 解析Json字符串
     * @param context
     * @param json
     */
    private void parseMessage(final Context context, String json) {
        JSONObject jo;
        try{
            jo = new JSONObject(json);
            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
            // 下线通知
            if (tag.equals(BmobConfig.TAG_OFFLINE)) {
                if (currentUser != null) {
                    //有监听对象
                    if (ehList.size() > 0){
                        for (EventListener handler : ehList) {
                            handler.onOffline();
                        }
                    } else {
                        //清空数据
                        CustomApplcation.getInstance().logout();
                    }
                }
            } else {
                // 获得发送者、接受者、发送时间
                String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
                final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
                String msgTime = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_MSGTIME);

                // 发送方不为空且不为黑名单用户
                if (fromId != null && !BmobDB.create(context, toId).isBlackUser(fromId)) {
                    // 不带tag标签--可接收陌生人消息
                    if (TextUtils.isEmpty(tag)) {
                        BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
                            @Override
                            public void onSuccess(BmobMsg bmobMsg) {
                                if (ehList.size() > 0) {
                                    for (int i = 0; i < ehList.size(); i++) {
                                        ((EventListener) ehList.get(i)).onMessage(bmobMsg);
                                    }
                                } else {
                                    boolean isAllow = CustomApplcation.getInstance().getSpUtil().
                                            isAllowPushNotify();
                                    // 当前登陆用户存在并且也等于接收方id
                                    if (isAllow && currentUser != null && currentUser.getObjectId().equals(toId)) {
                                        mNewNum++;
                                        showMsgNotify(context, bmobMsg);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                BmobLog.i("获取接收的消息失败：" + s);
                            }
                        });
                    } else {
                        // 带tag标签
                        // TAG_ADD_CONTACT:申请添加好友事件
                        if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
                            //保存好友请求到本地，并更新后台的未读字段
                            BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
                            if (currentUser != null) {
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (ehList.size() > 0) {
                                        for (EventListener handler : ehList) {
                                            handler.onAddUser(message);
                                        }
                                    } else {
                                        //标题:username;内容:"请求添加好友";跳转界面:NewNewFriendActivity
                                        showOtherNotify(context, message.getFromname(), toId,
                                                message.getFromname() + "请求添加好友",
                                                NewFriendActivity.class);
                                    }
                                }
                            }
                        } else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) { // TAG_ADD_AGREE:同意添加好友事件
                            String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
                            // 收到对方的同意请求之后，就得添加对方为好友
                            // 已默认添加同意方为好友，并保存到本地好友数据库
                            BmobUserManager.getInstance(context).addContactAfterAgree(username,
                                    new FindListener<BmobChatUser>() {
                                @Override
                                public void onSuccess(List<BmobChatUser> list) {
                                    //保存在内存中
                                    CustomApplcation.getInstance().setContactList(CollectionUtils.
                                            list2map(BmobDB.create(context).getContactList()));
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                            //显示通知
                            showOtherNotify(context, username, toId, username+"同意添加您为好友",
                                    MainActivity.class);
                            BmobMsg.createAndSaveRecentAfterAgree(context, json);
                        } else if (tag.equals(BmobConfig.TAG_READED)) { //TAG_READED:已读回执
                            String conversionId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_CONVERSIONID);
                            if (currentUser != null) {
                                BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
                                if (toId.equals(currentUser.getObjectId())) {
                                    if (ehList.size() > 0) {
                                        for (EventListener handler : ehList) {
                                            handler.onReaded(conversionId, msgTime);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
                    BmobChatManager.getInstance(context).updateMsgReaded(true, fromId, msgTime);
                    BmobLog.i("该消息发送方为黑名单用户");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
            BmobLog.i("parseMessage错误："+e.getMessage());
        }
    }


    /**
     * 显示与聊天消息的通知
     * Android 通知栏Notification的整合 全面学习 （一个DEMO让你完全了解它）
     * http://blog.csdn.net/vipzjyno1/article/details/25248021
     * @Title: showNotify
     * @return void
     * @throws
     */
    public void showMsgNotify(Context context, BmobMsg msg) {
        // 更新通知栏
        int icon = R.drawable.ic_launcher;
        String trueMsg = "";
        if (msg.getMsgType() == BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")) {
            trueMsg = "[表情]";
        } else if (msg.getMsgType()==BmobConfig.TYPE_IMAGE){
            trueMsg = "[图片]";
        } else if (msg.getMsgType()==BmobConfig.TYPE_VOICE){
            trueMsg = "[语音]";
        } else if (msg.getMsgType()==BmobConfig.TYPE_LOCATION){
            trueMsg = "[位置]";
        } else {
            // getContent()获取消息具体内容：根据类型存放：TEXT-字符串文本，IMAGE-图片地址、LOCATION-地理位置
            trueMsg = msg.getContent();
        }

        // getBelongUsername() 获取发送者userName-即Bmob账号
        CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
        String contentTitle = msg.getBelongUsername() + " (" + mNewNum + "条新消息)";

        Intent intent = new Intent(context, MainActivity.class);
        // 当这个Activity位于历史stack的顶端运行时，不再启动一个新的。
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();

        /*
        showNotifyWithExtras
        创建显示通知栏
        参数:
                icon:通知栏的图标 -
                tickerText：状态栏提示语 -
                contentTitle：通知标题 -
                contentText：通知内容 -
                targetClass：点击之后进入的Class
        */
        BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice, isAllowVibrate,
                icon, tickerText.toString(), contentTitle, tickerText.toString(), intent);
    }

    /**
     * 显示其他TAG通知
     * @param context context
     * @param username 标题
     * @param toId 接收方
     * @param ticker 通知内容
     */
    public void showOtherNotify(Context context, String username, String toId, String ticker,
                                Class<?> cls) {
        int icon = R.drawable.ic_launcher;

        boolean isAllowPush = CustomApplcation.getInstance().getSpUtil().isAllowPushNotify();
        boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
        if (isAllowPush && currentUser != null && currentUser.getObjectId().equals(toId)) {
            BmobNotifyManager.getInstance(context).showNotify(isAllowVoice, isAllowVibrate, icon,
                    ticker, username, ticker.toString(), cls);
        }
    }

}
