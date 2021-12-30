package helper;

import fs.trivial.boot.BootBlock;
import helper.annotation.FixedByteSerializer;
import org.junit.Assert;
import org.junit.Test;

/**
 * 定长编码序列化测试
 *
 * @author jiquanxi
 * @date 2021/12/30
 */
public class FixedByteSerializerTest {

    @Test
    public void testGetByteLength() {
        int bootByteLength = FixedByteSerializer.getSerializeLength(BootBlock.class);
        Assert.assertEquals(4, bootByteLength);
    }
}
