package fs.trivial;

import helper.annotation.SerializerOrder;
import org.junit.Test;
import util.StdOut;

import javax.naming.OperationNotSupportedException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Test class serializer
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
public class SerializerTest {

    @Test
    public void testClassToBytes() {
        String name = "C";
        Partition partition = new Partition();
        partition.setIndex(0);
        partition.setStart(0);
        partition.setEnd(1024 * 1024);
        partition.setNameLength(name.length());
        partition.setName(name);
        Class<Partition> partitionClass = Partition.class;
        Field[] fields = partitionClass.getDeclaredFields();
        NameValuePair<Integer, Field>[] pairs = new NameValuePair[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            StdOut.println(field.getName());
            StdOut.println(field.getType().getCanonicalName());

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
                    String val = (String) nameValuePair.getField().get(partition);
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
                    String val = (String) nameValuePair.getField().get(partition);
                    length += val.length();
                    buffer.put(val.getBytes(StandardCharsets.UTF_8));
                } else if (fieldType.getCanonicalName().equals("int")) {
                    int val = nameValuePair.getField().getInt(partition);
                    buffer.putInt(val);
                } else if (fieldType.getCanonicalName().equals("long")) {
                    long val = nameValuePair.getField().getLong(partition);
                    buffer.putLong(val);
                } else {
                    throw new OperationNotSupportedException("not support this type: " + fieldType.getCanonicalName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        buffer.flip();
        byte[] bytes = buffer.array();
        StdOut.println(bytes);
    }

    public static class NameValuePair<N, V> {
        private final N order;

        private final V field;

        public NameValuePair(final N order, final V field) {
            this.order = order;
            this.field = field;
        }

        public N getOrder() {
            return order;
        }

        public V getField() {
            return field;
        }
    }
}
