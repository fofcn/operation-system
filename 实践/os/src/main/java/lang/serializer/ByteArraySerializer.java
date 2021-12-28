package lang.serializer;

import helper.annotation.SerializerOrder;
import lang.NameValuePair;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

/**
 * serialize a class to byte array
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class ByteArraySerializer {

    /**
     * serialize class to byte array.
     * @param obj instance of classOfObj
     * @param classOfObj class info
     * @return byte array
     */
    public static byte[] serialize(Object obj, Class classOfObj) {
        Field[] fields = classOfObj.getDeclaredFields();
        NameValuePair<Integer, Field>[] pairs = new NameValuePair[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // We should get its value for ordering.
            SerializerOrder orderAnnotation = field.getAnnotation(SerializerOrder.class);
            int order = orderAnnotation.value();
            NameValuePair<Integer, Field> pair = new NameValuePair<>(order, field);
            pairs[i] = pair;
        }

        // sort by order
        Arrays.sort(pairs, Comparator.comparingInt(NameValuePair::getOrder));

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
}
