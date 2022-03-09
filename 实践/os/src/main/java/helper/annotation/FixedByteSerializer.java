package helper.annotation;

import lang.serializer.ByteArraySerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 字节随机化长度
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/30
 */
public interface FixedByteSerializer {
    /**
     * 获取序列化后的字节长度，针对变长编码无效
     * @param <T>
     * @param clazz
     * @return
     */
    static <T> int getSerializeLength(Class<T> clazz) {
        return ByteArraySerializer.evaluateByteLength(clazz);
    }
}
