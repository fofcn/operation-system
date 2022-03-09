package distributed.network.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * HTTP客户端工具类
 *
 * @author errorfatal89@gmail.com
 */
public final class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_XML = "text/xml";
    private static final long DEFAULT_TIMEOUT = 10000;
    private static final String DEFAULT_CHARSET = "UTF-8";

    private HttpUtil() {
    }

    /**
     * Get请求
     *
     * @param url      链接地址
     * @param paramMap 参数列表
     * @return 响应内容
     */
    public static String get(String url, Map<String, Object> paramMap) {
        return get(url, paramMap, (int) DEFAULT_TIMEOUT);
    }

    /**
     * Get请求
     *
     * @param url      链接地址
     * @param paramMap 参数列表
     * @param timeout  超时时间
     * @return 响应内容
     */
    public static String get(String url, Map<String, Object> paramMap, int timeout) {
        try {
            TinyHttpClientUtil.HttpResult httpResult = TinyHttpClientUtil.httpGet(url, null,
                    paramMap, DEFAULT_CHARSET, timeout);
            if (HttpURLConnection.HTTP_OK == httpResult.getCode()) {
                return httpResult.getContent();
            }
        } catch (IOException e) {
            logger.error("", e);
        }

        return null;
    }

    /**
     * post请求
     *
     * @param url      链接地址
     * @param paramMap 参数列表
     * @return 响应内容
     */
    public static String post(String url, Map<String, Object> paramMap) {
        try {
            TinyHttpClientUtil.HttpResult httpResult = TinyHttpClientUtil.httpPost(url, null,
                    paramMap, DEFAULT_CHARSET, DEFAULT_TIMEOUT);
            if (HttpURLConnection.HTTP_OK == httpResult.getCode()) {
                return httpResult.getContent();
            }
        } catch (IOException e) {
            logger.error("", e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
