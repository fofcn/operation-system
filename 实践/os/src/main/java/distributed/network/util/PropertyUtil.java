package distributed.network.util;


import java.io.*;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * properties文件工具类
 *
 * @author errorfatal89@gmail.com
 */
public class PropertyUtil {

    /**
     * 加载properties文件
     * @param file 文件路径
     * @return properties属性集合
     */
    public static Properties load(String file) {
        if (file == null) {
            return null;
        }
        return load(new File(file));
    }

    /**
     * 加载properties文件
     * @param file File对象
     * @return properties属性集合
     */
    public static Properties load(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            return load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * properties转换为对象
     * @param p 属性集合
     * @param object 对象
     */
    public static void properties2Object(Properties p, Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String mn = method.getName();
            if (mn.startsWith("set")) {
                try {
                    String tmp = mn.substring(4);
                    String first = mn.substring(3, 4);

                    String key = first.toLowerCase() + tmp;
                    String property = p.getProperty(key);
                    if (property != null) {
                        Class<?>[] pt = method.getParameterTypes();
                        if (pt != null && pt.length > 0) {
                            String cn = pt[0].getSimpleName();
                            Object arg = null;
                            if (cn.equals("int") || cn.equals("Integer")) {
                                arg = Integer.parseInt(property);
                            } else if (cn.equals("long") || cn.equals("Long")) {
                                arg = Long.parseLong(property);
                            } else if (cn.equals("double") || cn.equals("Double")) {
                                arg = Double.parseDouble(property);
                            } else if (cn.equals("boolean") || cn.equals("Boolean")) {
                                arg = Boolean.parseBoolean(property);
                            } else if (cn.equals("float") || cn.equals("Float")) {
                                arg = Float.parseFloat(property);
                            } else if (cn.equals("String")) {
                                arg = property;
                            } else {
                                continue;
                            }
                            method.invoke(object, arg);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * 加载properties文件
     * @param in 输入流
     * @return 属性集合
     */
    public static Properties load(InputStream in) {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedInputStream(in));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(in);
        }
    }

}
