package memory;

import org.junit.Assert;
import org.junit.Test;

/**
 * 数学工具测试类
 *
 * @author jiquanxi
 * @date 2021/12/20
 */
public class StdMathTest {

    @Test
    public void testNextPowerOf2() {
        int a = 1;
        Assert.assertEquals(1, StdMath.nextPowerOf2(a));
    }
}
