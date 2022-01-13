package cache;

import cache.lfu.LfuCache;
import cache.lfu.LfuCacheNode;
import cache.lru.LruCache;
import fs.trivial.SerializerTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.StdOut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * LRU缓存算法测试类
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class LfuCacheTest {

    private LfuCache<Integer, String> lfuCache;

    private final int capacity = 2;

//    private final String command = "\"LFUCache\",\"put\",\"put\",\"put\",\"put\",\"put\",\"get\",\"put\",\"get\",\"get\",\"put\",\"get\",\"put\",\"put\",\"put\",\"get\",\"put\",\"get\",\"get\",\"get\",\"get\",\"put\",\"put\",\"get\",\"get\",\"get\",\"put\",\"put\",\"get\",\"put\",\"get\",\"put\",\"get\",\"get\",\"get\",\"put\",\"put\",\"put\",\"get\",\"put\",\"get\",\"get\",\"put\",\"put\",\"get\",\"put\",\"put\",\"put\",\"put\",\"get\",\"put\",\"put\",\"get\",\"put\",\"put\",\"get\",\"put\",\"put\",\"put\",\"put\",\"put\",\"get\",\"put\",\"put\",\"get\",\"put\",\"get\",\"get\",\"get\",\"put\",\"get\",\"get\",\"put\",\"put\",\"put\",\"put\",\"get\",\"put\",\"put\",\"put\",\"put\",\"get\",\"get\",\"get\",\"put\",\"put\",\"put\",\"get\",\"put\",\"put\",\"put\",\"get\",\"put\",\"put\",\"put\",\"get\",\"get\",\"get\",\"put\",\"put\",\"put\",\"put\",\"get\",\"put\",\"put\",\"put\",\"put\",\"put\",\"put\",\"put\"";
    private final String command = null;
//    private final String data = "[10],[10,13],[3,17],[6,11],[10,5],[9,10],[13],[2,19],[2],[3],[5,25],[8],[9,22],[5,5],[1,30],[11],[9,12],[7],[5],[8],[9],[4,30],[9,3],[9],[10],[10],[6,14],[3,1],[3],[10,11],[8],[2,14],[1],[5],[4],[11,4],[12,24],[5,18],[13],[7,23],[8],[12],[3,27],[2,12],[5],[2,9],[13,4],[8,18],[1,7],[6],[9,29],[8,21],[5],[6,30],[1,12],[10],[4,15],[7,22],[11,26],[8,17],[9,29],[5],[3,4],[11,30],[12],[4,29],[3],[9],[6],[3,4],[1],[10],[3,29],[10,28],[1,20],[11,13],[3],[3,12],[3,8],[10,9],[3,26],[8],[7],[5],[13,17],[2,27],[11,15],[12],[9,19],[2,15],[3,16],[1],[12,17],[9,1],[6,19],[4],[5],[5],[8,1],[11,7],[5,2],[9,28],[1],[2,2],[7,4],[4,22],[7,24],[9,26],[13,28],[11,26]";
    private final String data = null;
    private final Pattern pattern = Pattern.compile("\\[(\\d+,)?\\d+]");

    List<String> funcNameList;
    List<String[]> dataList;

    @Before
    public void before() {
        dataList = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            int counter = 0;
            Matcher m = pattern.matcher(data);
            while (m.find()) {
                String d = data.substring(m.start(), m.end()).replace("[", "").replace("]", "");
                if (counter == 0) {
                    Integer capacity = Integer.parseInt(d);
                    lfuCache = new LfuCache<>(capacity);
                } else {
                    String[] inputs = d.split(",");
                    dataList.add(inputs);
                }
                counter++;
            }
        }

        funcNameList = new ArrayList<>();
        // 解析命令
        if (command != null && !command.isEmpty()) {
            String[] funcs = command.split(",");
            if (funcs != null && funcs.length > 0) {
                for (int i = 0; i < funcs.length; i++) {
                    if (i == 0) {
                        continue;
                    }

                    String funcName = funcs[i].replace("\"", "");
                    if (funcName.equals("put")) {
                        funcName = "set";
                    } else if (funcName.equals("get")) {
                        funcName = "get";
                    }
                    funcNameList.add(funcName);

                }
            }
        } else {
            StdOut.println("new lfu cache");
            lfuCache = new LfuCache<>(capacity);
        }
    }

    @After
    public void after() {
        lfuCache.clear();
    }

    @Test
    public void testLeetCodeTestCase() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.println("null");
        for (int i = 0; i < funcNameList.size(); i++) {
            String funcName = funcNameList.get(i);
            String[] arguments = dataList.get(i);
            Integer key = Integer.parseInt(arguments[0]);

            Object ret = invokeMethod(funcName, key, arguments);

            if ("set".equals(funcName)) {
                System.out.println(funcName + "->[" + arguments[0] + ", " + arguments[1] + "] ->" + ret);
            } else {
                System.out.println(funcName + "->[" + arguments[0] + "] ->" + ret);
            }
        }
    }

    @Test
    public void testSetAndSetLeetCode() {
        lfuCache = new LfuCache<>(3);
        lfuCache.set(2, "2");
        lfuCache.set(1, "1");

        String str = lfuCache.get(2);
        Assert.assertEquals("2", str);

        str = lfuCache.get(1);
        Assert.assertEquals("1", str);

        str = lfuCache.get(2);
        Assert.assertEquals("2", str);

        lfuCache.set(3, "3");

        lfuCache.set(4, "4");

        str = lfuCache.get(3);
        Assert.assertNull(str);

        str = lfuCache.get(2);
        Assert.assertEquals("2", str);

        str = lfuCache.get(1);
        Assert.assertEquals("1", str);

        str = lfuCache.get(4);
        Assert.assertEquals("4", str);
    }

    @Test
    public void testSetAndGet() {
        lfuCache = new LfuCache<>(2);
        // 预期： [frequency = 1]->[[key = 1, value = 1]]
        // 实际： {frequency=1}LfuCacheNode{frequency=1, key=1, value=1}
        // 结果： 正确
        lfuCache.set(1, "1");
        // 预期： [frequency = 1]->[[key = 1, value = 1], [key = 2, value = 2]]
        // 实际： {frequency=1}LfuCacheNode{frequency=1, key=1, value=1}LfuCacheNode{frequency=1, key=2, value=2}"
        // 结果： 正确
        lfuCache.set(2, "2");

        // 预期：
        // [frequency = 1]->[[key = 2, value = 2]]
        // [frequency = 2]->[[key = 1, value = 1]]
        // 实际：
        // {frequency=1}LfuCacheNode{frequency=1, key=2, value=2}
        // {frequency=2}LfuCacheNode{frequency=2, key=1, value=1}
        // 结果： 正确
        String str = lfuCache.get(1);
        Assert.assertEquals("1", str);
        StdOut.println("get cache from lfucache: key=1, value = " + str);

        // 预期：
        // [frequency = 1]->[[key = 3, value = 3]]
        // [frequency = 2]->[[key = 1, value = 1]]
        // 实际：
        // {frequency=1}LfuCacheNode{frequency=1, key=3, value=3}
        // {frequency=2}LfuCacheNode{frequency=2, key=1, value=1}
        // 结果： 正确
        lfuCache.set(3, "3");

        // 不会改动结构
        str = lfuCache.get(2);
        Assert.assertNull(str);
        StdOut.println("get cache from lfucache: key=2, value = null");

        // 预期：
        // [frequency = 2]->[[key = 1, value = 1], [key = 3, value = 3]]
        // 实际
        // {frequency=2}LfuCacheNode{frequency=2, key=1, value=1}LfuCacheNode{frequency=2, key=3, value=3}
        // 结果: 正确
        str = lfuCache.get(3);
        StdOut.println("get cache from lfucache: key=3, value = " + str);

        // 预期：
        // [frequency = 1]->[ [key = 4, value = 4]]
        // [frequency = 2]->[ [key = 3, value = 3]]
        // 实际：
        // {frequency=1}LfuCacheNode{frequency=1, key=4, value=4}
        // {frequency=2}LfuCacheNode{frequency=2, key=3, value=3}
        // 结果： 正确
        lfuCache.set(4, "4");

        // 预期： 返回空，结构不变
        str = lfuCache.get(1);
        Assert.assertNull(str);
        StdOut.println("get cache from lfucache: key=1, value = null");

        // 预期：
        // [frequency = 1]->[ [key = 4, value = 4]]
        // [frequency = 3]->[ [key = 3, value = 3]]
        // 实际：
        // {frequency=1}LfuCacheNode{frequency=1, key=4, value=4}
        // {frequency=3}LfuCacheNode{frequency=3, key=3, value=3}
        // 结果： 正确
        str = lfuCache.get(3);
        Assert.assertEquals("3", str);
        StdOut.println("get cache from lfucache: key=3, value = " + str);

        // 预期：
        // [frequency = 2]->[ [key = 4, value = 4]]
        // [frequency = 3]->[ [key = 3, value = 3]]
        // 实际：
        // {frequency=2}LfuCacheNode{frequency=2, key=4, value=4}
        // {frequency=3}LfuCacheNode{frequency=3, key=3, value=3}
        // 结果： 正确
        str = lfuCache.get(4);
        StdOut.println("get cache from lfucache: key=4, value = " + str);
    }

    @Test
    public void testNormalSet() {
        lfuCache.set(1, "1");
        Assert.assertEquals(1, lfuCache.size());
    }

    @Test
    public void testNormalGet() {
        String str = lfuCache.get(1);
        Assert.assertNull(str);
    }

    @Test
    public void testNormalSetAndGet() {
        for (int i = 0; i < capacity; i++) {
            lfuCache.set(i, "" + i);
        }

        for (int i = 0; i < capacity; i++) {
            String val = lfuCache.get(i);
            StdOut.println("Key: " + i + ", value: " + lfuCache.get(i));
            Assert.assertEquals("" + i, val);
        }
    }


    @Test
    public void testRemoveFrontByGet() {
        for (int i = 0; i < capacity; i++) {
            lfuCache.set(i, "" + i);
        }

        // 第一次获取key为10
        // 缓存顺序应该为10,0,...

    }

    @Test
    public void testOverrideFromEnd() {
        int overrideCount = 10;
        for (int i = 0; i < capacity + overrideCount; i++) {
            if (capacity == i) {
                StdOut.println("");
            }

            lfuCache.set(i, "" + i);
        }

        for (int i = 0; i < capacity + overrideCount; i++) {
            String val = lfuCache.get(i);
            StdOut.println("Key: " + i + ", value: " + lfuCache.get(i));
            if (i < overrideCount) {
                Assert.assertNull(val);
            } else {
                Assert.assertEquals("" + i, val);
            }
        }
    }

    @Test
    public void testNormalParallelSetAndGet() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(capacity * 2);
        IntStream.range(0, capacity).<Runnable>mapToObj(key -> () -> {
                lfuCache.set(key, UUID.randomUUID().toString());
            countDownLatch.countDown();
        }).forEach(executorService::execute);

        IntStream.range(0, capacity).<Runnable>mapToObj(key -> () -> {
                lfuCache.get(key);
            countDownLatch.countDown();
        }).forEach(executorService::execute);
        countDownLatch.await();

        StdOut.println("LruCache Size: " + lfuCache.size());
        Assert.assertEquals(lfuCache.size(), capacity);

        for (int i = 0; i < capacity; i++) {
            StdOut.println("key: " + i + ", value: " + lfuCache.get(i));
        }
    }

    @Test
    public void testEvictParallelSetAndGet() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(capacity * 2);
        IntStream.range(0, capacity * 2).<Runnable>mapToObj(key -> () -> {
            lfuCache.set(key, UUID.randomUUID().toString());
            lfuCache.set(key, UUID.randomUUID().toString());
            countDownLatch.countDown();
        }).forEach(executorService::execute);

        IntStream.range(capacity, capacity * 2).<Runnable>mapToObj(key -> () -> {
            StdOut.println(lfuCache.get(key));
            countDownLatch.countDown();
        }).forEach(executorService::execute);
        countDownLatch.await();

        StdOut.println("LruCache Size: " + lfuCache.size());
        for (int i = capacity; i < capacity * 2; i++) {
            StdOut.println("key: " + i + ", value: " + lfuCache.get(i));
        }
        Assert.assertEquals(lfuCache.size(), capacity);

        lfuCache.clear();
        Assert.assertEquals(lfuCache.size(), 0);
        executorService.shutdown();
    }

    private Object invokeMethod(String funcName, Integer key, String[] arguments) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        Method[] methods = LfuCache.class.getDeclaredMethods();
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(funcName)) {
                method = methods[j];
            }
        }
        if (method == null) {
            return null;
        }

        Object ret = null;
        if (method.getParameterCount() > 1) {
            ret = method.invoke(lfuCache, key, arguments[1]);
        } else {
            ret = method.invoke(lfuCache, key);
        }

        if (method.getReturnType().getName().equals("void")) {
            ret = null;
        }

        return ret;
    }
}

