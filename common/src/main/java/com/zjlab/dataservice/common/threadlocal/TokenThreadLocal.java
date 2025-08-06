package com.zjlab.dataservice.common.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;

public class TokenThreadLocal {

    private static final ThreadLocal<String> TOKEN = new TransmittableThreadLocal<>();

    public static void setToken(String userId){
        TOKEN.set(userId);

    }

    public static String getToken(){
        return TOKEN.get();
    }

    public static void remove(){
        TOKEN.remove();
    }
}
