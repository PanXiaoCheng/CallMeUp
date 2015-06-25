package com.mrpan.callmeup.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.bmob.im.bean.BmobChatUser;

/**
 * Created by skywish on 2015/6/24.
 */
public class CollectionUtils {

    /**
     * collection 是否为空  定义为<?>的实例只能使用其中的查询和删除操作，不能使用新增操作
     * @param collection
     * @return
     */
    public static boolean isNotNull(Collection<?> collection) {
        if (collection != null && collection.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * list转map key为用户名
     * @param users
     * @return
     */
    public static Map<String, BmobChatUser> list2map (List<BmobChatUser> users) {
        Map<String, BmobChatUser> friends = new HashMap<>();
        for (BmobChatUser user : users) {
            friends.put(user.getUsername(), user);
        }
        return friends;
    }

    public static List<BmobChatUser> map2list(Map<String, BmobChatUser> maps) {
        List<BmobChatUser> users = new ArrayList<>();
        Iterator<Entry<String, BmobChatUser>> iterator = maps.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, BmobChatUser> entry = iterator.next();
            users.add(entry.getValue());
        }
        //entrySet 比 keySet 快
//        Iterator it = maps.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next().toString();
//            users.add(maps.get(key));
//        }
        return users;
    }
}
