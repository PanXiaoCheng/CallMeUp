package com.mrpan.callmeup.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by skywish on 2015/6/24.
 * Android之SharedPreferences的一个工具类
 * http://blog.csdn.net/way_ping_li/article/details/8061838
 */
public class SharedPreferenceUtil {
    private SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private String SHARED_KEY_NOTIFY = "shared_key_notify";
    private String SHARED_KEY_VOICE = "shared_key_sound";
    private String SHARED_KEY_VIBRATE = "shared_key_vibrate";

    public SharedPreferenceUtil(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 是否允许推送，默认允许
     * @return
     */
    public boolean isAllowPushNotify() {
        return  sp.getBoolean(SHARED_KEY_NOTIFY, true);
    }

    /**
     * 设置是否可以推送
     * @param isAllowed
     */
    public void setPushNotify(boolean isAllowed) {
        editor.putBoolean(SHARED_KEY_NOTIFY, isAllowed);
        editor.commit();
    }

    /**
     * 是否允许声音
     * @return
     */
    public boolean isAllowVoice() {
        return sp.getBoolean(SHARED_KEY_VOICE, true);
    }

    public void setAllowVoice(boolean isAllowed) {
        editor.putBoolean(SHARED_KEY_VOICE, isAllowed);
        editor.commit();
    }

    /**
     * 是否允许振动，默认允许
     * @return
     */
    public boolean isAllowVibrate() {
        return sp.getBoolean(SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrate(Boolean isAllowed) {
        editor.putBoolean(SHARED_KEY_VIBRATE, isAllowed);
        editor.commit();
    }
}
