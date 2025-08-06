package com.zjlab.dataservice.common.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * filter获取用户id
 */
public class UserThreadLocal {

    private static final ThreadLocal<String> USER_ID = new TransmittableThreadLocal<>();

    public static void setUserId(String userId){
        USER_ID.set(userId);

    }

    public static String getUserId(){
        return USER_ID.get();
    }

    public static void remove(){
        USER_ID.remove();
    }

}
