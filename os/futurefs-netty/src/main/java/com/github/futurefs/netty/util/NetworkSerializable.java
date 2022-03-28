package com.github.futurefs.netty.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author errorfatal89@gmail.com
 */
public class NetworkSerializable {
    private static final Logger log = LoggerFactory.getLogger("Network");
    public static <T> List<T> jsonArrayDecode(byte[] bytes, Class<T> classType) {
        if (bytes == null || classType == null) {
            return null;
        }

        try {
            String json = new String(bytes, "utf-8");
            return (List<T>)JSON.parseObject(json, classType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static <T> T jsonDecode(byte[] bytes, Class<T> classType) {
        if (bytes == null || classType == null) {
            return null;
        }

        try {
            String json = new String(bytes, "utf-8");
            return JSON.parseObject(json, classType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> List<T> jsonDecodeArray(byte[] bytes, Class<T> classType) {
        if (bytes == null || classType == null) {
            return null;
        }

        try {
            String json = new String(bytes, "utf-8");
            return JSON.parseArray(json, classType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static <T> byte[] jsonEncode(T obj) {
        try {
            String jsonStr = JSON.toJSONString(obj);
            return jsonStr.getBytes("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
        }
        return null;
    }
}
