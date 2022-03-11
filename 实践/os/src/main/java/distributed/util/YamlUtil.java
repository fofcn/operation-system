package distributed.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * YAML解析
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
@Slf4j
public class YamlUtil {

    /**
     * 将yaml配置转换为对象
     * @param clazz 类定义
     * @param path 文件路径
     * @param <T>
     * @return java对象
     */
    public static <T> T readObject(Class<T> clazz, String path) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(new File(path), clazz);
        } catch (JsonProcessingException e) {
            log.error("json process error, value: {}", path, e);
        } catch (IOException e) {
            log.error("json process error, value: {}", path, e);
        }

        return null;
    }

    /**
     * 将yaml配置转换为对象
     * @param clazz 类定义
     * @param text 内容
     * @param <T>
     * @return java对象
     */
    public static <T> T readObject(String text, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(new File(text), clazz);
        } catch (JsonProcessingException e) {
            log.error("json process error, value: {}", text, e);
        } catch (IOException e) {
            log.error("json process error, value: {}", text, e);
        }

        return null;
    }
}
