package lang.serializer;

import helper.annotation.SerializerOrder;
import lang.NameValuePair;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * serialize a class to byte array
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
public class ByteArraySerializer {

    /**
     * 评估定长编码的类的字节长度
     * @param classOfObj
     * @return
     */
    public static int evaluateByteLength(Class classOfObj) {
        List<NameValuePair<Integer, Field>> pairs = getNameValuePairs(classOfObj);

        // fill bytes to buffer
        int length = 0;
        for (NameValuePair<Integer, Field> nameValuePair : pairs) {
            try {
                if (!nameValuePair.getField().isAccessible()) {
                    nameValuePair.getField().setAccessible(true);
                }

                // get this field's type
                Class<?> fieldType = nameValuePair.getField().getType();
                if (fieldType.getCanonicalName().equals("java.lang.String")) {
                } else if (fieldType.getCanonicalName().equals("int")) {
                    length += Integer.SIZE / 8;
                } else if (fieldType.getCanonicalName().equals("long")) {
                    length += Long.SIZE / 8;
                } else {
                    throw new OperationNotSupportedException("not support this type: " + fieldType.getCanonicalName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return length;
    }

    /**
     * serialize class to byte array.
     * @param obj instance of classOfObj
     * @param classOfObj class info
     * @return byte array
     */
    public static byte[] serialize(Object obj, Class classOfObj) {
        List<NameValuePair<Integer, Field>> pairs = getNameValuePairs(classOfObj);

        // fill bytes to buffer
        int length = 0;
        for (NameValuePair<Integer, Field> nameValuePair : pairs) {
            try {
                if (!nameValuePair.getField().isAccessible()) {
                    nameValuePair.getField().setAccessible(true);
                }

                // get this field's type
                Class<?> fieldType = nameValuePair.getField().getType();
                if (fieldType.getCanonicalName().equals("java.lang.String")) {
                    String val = (String) nameValuePair.getField().get(obj);
                    length += val.length();
                } else if (fieldType.getCanonicalName().equals("int")) {
                    length += 4;
                } else if (fieldType.getCanonicalName().equals("long")) {
                    length += 8;
                } else {
                    throw new OperationNotSupportedException("not support this type: " + fieldType.getCanonicalName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        ByteBuffer buffer = ByteBuffer.allocate(length);
        for (NameValuePair<Integer, Field> nameValuePair : pairs) {
            // 建立order和field的映射
            try {
                if (!nameValuePair.getField().isAccessible()) {
                    nameValuePair.getField().setAccessible(true);
                }

                // get this field's type
                Class<?> fieldType = nameValuePair.getField().getType();
                if (fieldType.getCanonicalName().equals("java.lang.String")) {
                    String val = (String) nameValuePair.getField().get(obj);
                    length += val.length();
                    buffer.put(val.getBytes(StandardCharsets.UTF_8));
                } else if (fieldType.getCanonicalName().equals("int")) {
                    int val = nameValuePair.getField().getInt(obj);
                    buffer.putInt(val);
                } else if (fieldType.getCanonicalName().equals("long")) {
                    long val = nameValuePair.getField().getLong(obj);
                    buffer.putLong(val);
                } else {
                    throw new OperationNotSupportedException("not support this type: " + fieldType.getCanonicalName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        buffer.flip();
        return buffer.array();
    }

    public static <T> T deserialize(Class<T> classOfT, byte[] bytes) {
        List<NameValuePair<Integer, Field>> nameValuePairs = getNameValuePairs(classOfT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        T t = null;
        try {
            t = classOfT.newInstance();
            int strLength = -1;
            for (NameValuePair<Integer, Field> pair : nameValuePairs) {
                pair.getField().setAccessible(true);
                // get this field's type
                Class<?> fieldType = pair.getField().getType();
                if (fieldType.getCanonicalName().equals("java.lang.String")) {
                    if (strLength == -1) {
                        throw new IllegalArgumentException("string length is unavailable.");
                    }
                    byte[] val = new byte[strLength];
                    byteBuffer.get(val);
                    String str = new String(val, StandardCharsets.UTF_8);
                    pair.getField().set(t, str);
                } else if (fieldType.getCanonicalName().equals("int")) {
                    int val = byteBuffer.getInt();
                    pair.getField().set(t, val);
                    strLength = val;
                } else if (fieldType.getCanonicalName().equals("long")) {
                    long val = byteBuffer.getLong();
                    pair.getField().set(t, val);
                    strLength = (int) val;
                } else {
                    throw new OperationNotSupportedException("not support this type: " + fieldType.getCanonicalName());
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (OperationNotSupportedException e) {
            e.printStackTrace();
        }

        return t;
    }

    private static List<NameValuePair<Integer, Field>> getNameValuePairs(Class classOfObj) {
        Field[] fields = classOfObj.getDeclaredFields();
        List<NameValuePair<Integer, Field>> pairs = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // We should get its value for ordering.
            SerializerOrder orderAnnotation = field.getAnnotation(SerializerOrder.class);
            if (orderAnnotation == null) {
                continue;
            }

            int order = orderAnnotation.value();
            NameValuePair<Integer, Field> pair = new NameValuePair<>(order, field);
            pairs.add(pair);
        }

        // sort by order
        pairs.sort(Comparator.comparingInt(NameValuePair::getOrder));
        return pairs;
    }
}
