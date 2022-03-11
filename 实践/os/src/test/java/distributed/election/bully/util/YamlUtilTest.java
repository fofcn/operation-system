package distributed.election.bully.util;

import distributed.election.bully.config.BullyConfig;
import distributed.util.YamlUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * YamlUtil test
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
public class YamlUtilTest {

    @Test
    public void testReadObject() {
        BullyConfig bullyConfig = YamlUtil.readObject(BullyConfig.class, "G:\\github.com\\fofcn\\operation-system\\实践\\os\\src\\main\\resources\\election\\bully/bully.yml");
        Assert.assertNotNull(bullyConfig);
        Assert.assertNotNull(bullyConfig.getClusterNodes());
    }
}
