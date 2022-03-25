package com.moutuan.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * jsoup测试工具类
 *
 * @author jiquanxi
 * @date 2022/03/21
 */
public class JsoupTester {

    public static void main(String[] args) throws IOException {
        String str = "eJydlGtvmzAUhv+LpeQTCjbGYCJFUy6jzdqkXZe2a7sJEeoQknApmKQX9b/vAIF007ppk1D0nIvf8/qivKB0fI+6L2ixcX3UJRhjnb4qaCtS1EWkgzsGUpDMoGToJte5xRnhloK8Q87C2MKEK2ieXo1Q945jrBgYfy8SFxDfEUvDCsEcUjXrwJoOX9E1hia0lDLJuqoqEjfLkjiVnVAEMnejjheHqut5cR5JNY8CGWzFJvaD6MPcd7I4Tz3Ro+1MpNsAaOcGoRu0k40rF3Ea9rS2F0cyiHLRayZ0qqa3A1qaHYmdsxAApXhLowe0a432RiykM4+ljENnE0TrHhT33oD27rLAj/KkRe3GYIuOQM7Ym4SocgCp2ijkNAjFoxwu3SgSm6ZJVGmnUHXyxPF+U6/9QbLcZYv2wQ587+x1qxW1+qSBC/HLpJ0G/vLv+0uFF8Pvf23wH52yw72w5jpYcTdvQ3vvCJ7qny5ZraTUch2ClxfOipdHTaoQbsJiampARkmkIXivnBVkWEB6Q7QhUhIHwjWZVkO8JBPIaKjSM4AqFQZEajKqtTpQtQL8GdVc8GdUfeCPlZ4N8MdKFQZeGK5JL6sMHOisoaoPHNByBoNptJzBYBqtqjCDaiWRXwiObF0e2ZUynp5fzqD0LceYkJ9DTRmcjW4geqcNZNy3J68MLmezs+lhULPMJEzRsKUczyanRRmbCrTXVcp1hbODqCxEC/EJ/HdBvXjUQOLTTj7xjyf+sD/pTwfqRKxiMz6Kztdfjp6Xz/Fo+vBZXRnxLFfxV3s1P7NX2thLAvskfRDXuM+G908Xqnt/3ad2YtkDd3I75GNz07fGKnk6lqtw6fo4DHfr09vzmzwd03UPvf4ApQ2JGw==";
        byte[] bytes = decompress(Base64.decodeBase64(str));
        String decompressStr = new String(bytes, StandardCharsets.UTF_8);
        JSONObject jsonObj = JSON.parseObject(decompressStr);
        long ts = System.currentTimeMillis();
        long cts = ts + 100 * 1000;
        jsonObj.put("ts", ts);
        jsonObj.put("cts", cts);
        String newToken = JSON.toJSONString(jsonObj);
        byte[] newTokenBytes = compress(newToken.getBytes(StandardCharsets.UTF_8));
        String newTokenStr = Base64.encodeBase64String(newTokenBytes);

        String payload = "{\"login\":\"wmgbbl339799\",\"part_key\":\"\",\"password\":\"ljbf8687\",\"error\":\"\",\"success\":\"\",\"isFetching\":false,\"loginType\":\"account\",\"verifyRequestCode\":\"\",\"verifyResponseCode\":\"\",\"captchaCode\":\"\",\"verifyType\":null,\"rohrToken\":\""+newTokenStr+"\"}";
        String postUrl = "https://epassport.meituan.com/api/account/login?service=waimai&bg_source=3&loginContinue=https:%2F%2Fe.waimai.meituan.com%2Fnew_fe%2Flogin%23%2Flogin%2Fcontinue&loginType=account";
        String response = Jsoup.connect(postUrl).timeout(60 * 1000).ignoreContentType(true)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36")
                .header("Referer", "https://epassport.meituan.com/account/unitivelogin?bg_source=3&service=waimai&platform=2&continue=https://e.waimai.meituan.com%2Fnew_fe%2Flogin%23%2Flogin%2Fcontinue&left_bottom_link=%2Faccount%2Funitivesignup%3Fbg_source%3D3%26service%3Dwaimai%26platform%3D2%26extChannel%3Dwaimaie%26ext_sign_up_channel%3Dwaimaie%26continue%3Dhttps%3A%2F%2Fe.waimai.meituan.com%2Fv2%2Fepassport%2FsignUp&right_bottom_link=%2Faccount%2Funitiverecover%3Fbg_source%3D3%26service%3Dwaimai%26platform%3D2%26continue%3Dhttps%3A%2F%2Fe.waimai.meituan.com%252Fnew_fe%252Flogin%2523%252Flogin%252Frecover")
                .header("Host", "e.waimai.meituan.com")
                .header("cookie", "_ga=GA1.2.1135312240.1641981492; _lxsdk_cuid=17fab547eb6c8-0f20559de583f8-9771a3f-1fa400-17fab547eb6c8; wm_order_channel=default; request_source=openh5; au_trace_key_net=default; _lxsdk=17fab547eb6c8-0f20559de583f8-9771a3f-1fa400-17fab547eb6c8; openh5_uuid=17fab547eb6c8-0f20559de583f8-9771a3f-1fa400-17fab547eb6c8; uuid=17fab547eb6c8-0f20559de583f8-9771a3f-1fa400-17fab547eb6c8; m_grayrelease_feepassport=true; cssVersion=8f982056; _lx_utm=utm_source%3D; e_u_id_3299326472=44d2b22bfd9d295bd0fd2fa8317c73a0; eplt=lLZyBBitxPOtbqG9N5UDRP8jNaR-WO7uFmy6Ct6nD5JanpUc3GLkfzSGwbpVp1Aqh_aTJ7alwfN4n101R0NlZw; eprt=FLtzkvxBVP9oDMDgV_g91aTFTjAijUWSJ1W_ZputXJ6mmD3w0LzML15YerqSRt18Zti58uT1BAmTAUikjDzQ0g; logan_session_token=ij8o25yt3y2d9bxc5tyt; _lxsdk_s=17fafaf7d88-7c1-1c9-03a%7C%7C42")
                .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-User", "?1")
                .header("Upgrade-Insecure-Requests", "1")
                .header("x-requested-with", "XMLHttpRequest")
                .method(Connection.Method.POST)
                .data("payload", payload)
                .execute()
                .body();
        System.out.println(response);
    }

    /**
     * 解压缩
     *
     * @param data
     *            待压缩的数据
     * @return byte[] 解压缩后的数据
     */
    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }

    /**
     * 压缩
     *
     * @param data
     *            待压缩数据
     * @return byte[] 压缩后的数据
     */
    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

}
