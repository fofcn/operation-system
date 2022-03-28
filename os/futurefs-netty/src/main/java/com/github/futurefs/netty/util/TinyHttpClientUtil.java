package com.github.futurefs.netty.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * 轻量HTTP 客户端
 *
 * @author errorfatal89@gmail.com
 */
public final class TinyHttpClientUtil {

    private TinyHttpClientUtil() {}

    /**
     * GET请求
     * @param url 链接地址
     * @param headers 协议头
     * @param paramValues 请求参数
     * @param encoding 字符编码
     * @param readTimeoutMs 超时时间
     * @return
     * @throws IOException 读取错误
     */
    public static HttpResult httpGet(String url, Map<String, String> headers, Map<String, Object> paramValues,
                                     String encoding, long readTimeoutMs) throws IOException {
        String encodedContent = encodingParams(paramValues, encoding);
        url += (null == encodedContent) ? "" : ("?" + encodedContent);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout((int) readTimeoutMs);
            conn.setReadTimeout((int) readTimeoutMs);
            setHeaders(conn, headers, encoding);

            conn.connect();
            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IOUtil.toString(conn.getInputStream(), encoding);
            } else {
                resp = IOUtil.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 对参数进行编码
     * @param paramValues 参数列表
     * @param encoding 字符编码
     * @return
     * @throws UnsupportedEncodingException 不支持该字符编码
     */
    private static String encodingParams(Map<String, Object> paramValues, String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == paramValues) {
            return null;
        }

        for (Iterator<Map.Entry<String, Object>> iter = paramValues.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, Object> entry = iter.next();
            if (entry.getValue() != null) {
                sb.append(entry.getKey()).append("=");
                sb.append(URLEncoder.encode(entry.getValue().toString(), encoding));
            }

            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 设置协议头
     * @param conn 链接
     * @param headers 协议头
     * @param encoding 字符编码
     */
    private static void setHeaders(HttpURLConnection conn, Map<String, String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<Map.Entry<String, String>> iter = headers.entrySet().iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next().getKey(), iter.next().getValue());
            }
        }
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

        String ts = String.valueOf(System.currentTimeMillis());
        conn.addRequestProperty("TinyHttp-Client-RequestTS", ts);
    }

    /**
     * POST请求
     * @param url 链接地址
     * @param headers 协议头
     * @param paramValues 请求参数
     * @param encoding 字符编码
     * @param readTimeoutMs 超时时间
     * @return
     * @throws IOException 读取错误
     */
    public static HttpResult httpPost(String url, Map<String, String> headers, Map<String, Object> paramValues,
                                      String encoding, long readTimeoutMs) throws Exception {
        String encodedContent = encodingParams(paramValues, encoding);

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout((int) readTimeoutMs);
            conn.setReadTimeout((int) readTimeoutMs);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            setHeaders(conn, headers, encoding);

            if (encodedContent != null) {
                conn.getOutputStream().write(encodedContent.getBytes("UTF-8"));
            }

            int respCode = conn.getResponseCode();
            String resp = null;

            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = IOUtil.toString(conn.getInputStream(), encoding);
            } else {
                resp = IOUtil.toString(conn.getErrorStream(), encoding);
            }
            return new HttpResult(respCode, resp);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    public static class HttpResult {
        private final int code;
        private final String content;

        public HttpResult(int code, String content) {
            this.code = code;
            this.content = content;
        }

        public int getCode() {
            return code;
        }

        public String getContent() {
            return content;
        }
    }
}
