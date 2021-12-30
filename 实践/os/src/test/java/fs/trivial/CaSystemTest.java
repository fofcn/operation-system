package fs.trivial;

import org.junit.Test;

/**
 * Test for CaSystem
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class CaSystemTest {

    @Test
    public void testInitialize() {
        Partition partition = new Partition();
        partition.setName("C");
        partition.setNameLength(1);
        partition.setIndex(0);
        partition.setStart(0L);
        partition.setEnd(1024L * 1024);
        CaSystem caSystem = new CaSystem("ca_fs_test_disk", partition, 1 * 1024);
        caSystem.initialize();
    }
}
