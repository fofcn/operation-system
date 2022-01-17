package lang;

import lang.skiplists.SkipLists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        skipList.put(1, 1);

        int ret = skipList.get(1);
        Assert.assertEquals(1, ret);

        Integer nullRet = skipList.get(2);
        Assert.assertNull(nullRet);
    }
}
