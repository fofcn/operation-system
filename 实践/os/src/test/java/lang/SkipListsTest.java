package lang;

import lang.skiplists.SkipLists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.StdOut;

/**
 * 跳表测试
 *
 * @author jiquanxi
 * @date 2022/01/17
 */
public class SkipListsTest {

    private SkipLists<Integer, Integer> skipList;

    @Before
    public void before() {
        skipList = new SkipLists<>();
    }

    @Test
    public void testNormalPut() {
        Integer[] input = {3, 6, 7, 9, 12, 19, 21, 25, 26};

        // 添加元素
        for (int i = 0; i < input.length; i++) {
            skipList.put(input[i], input[i]);
        }

        // 获取所有元素
        for (int i = 0; i < input.length; i++) {
            Integer val = skipList.get(input[i]);
            Assert.assertEquals(input[i], val);
            StdOut.println("key=" + input[i] + ", value=" + val);
        }

        Integer val;
        // 删除所有元素
        for (int i = 0; i < input.length; i++) {
            skipList.delete(input[i]);
        }

        // 删除完尝试获取一个元素
        val = skipList.get(3);
        Assert.assertNull(val);
    }
}
