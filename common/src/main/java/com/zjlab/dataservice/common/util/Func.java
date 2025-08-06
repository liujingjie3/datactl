package com.zjlab.dataservice.common.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class Func {
    private static final ObjectMapper mapper = new ObjectMapper();
    /**
     * 判断对象是否非空（不是 null）
     */
    public static boolean notNull(Object obj) {
        return obj != null;
    }

    /**
     * 判断对象是否为空（是 null）
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("toJson error", e);
        }
    }

    public static <T> T readJson(String json, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("JSON 解析失败: " + json, e);
        }
    }

    @SafeVarargs
    public static <T> boolean isAllEmpty(T... args) {
        if (args == null || args.length == 0) {
            return true;
        }
        for (T arg : args) {
            if (arg != null && !(arg instanceof String && ((String) arg).trim().isEmpty())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false; // 对象类型不支持 isEmpty，则默认返回 false
    }

}
