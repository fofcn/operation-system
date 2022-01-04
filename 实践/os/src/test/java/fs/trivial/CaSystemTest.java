package fs.trivial;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.StdOut;

import java.util.List;

/**
 * Test for CaSystem
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class CaSystemTest {

    private CaSystem caSystem;

    @Before
    public void before() {
        Partition partition = new Partition();
        partition.setName("C");
        partition.setNameLength(1);
        partition.setIndex(0);
        partition.setStart(0L);
        partition.setEnd(1024L * 1024);
        caSystem = new CaSystem("ca_fs_test_disk", partition, 1 * 1024);
        caSystem.initialize();
    }

    @After
    public void after() {

    }

    @Test
    public void testInitialize() {
        caSystem.createFile("jiquanxi1.txt");
        caSystem.createFile("jiquanxi2.txt");
        caSystem.createFile("jiquanxi3.txt");
        caSystem.createFile("jiquanxi4.txt");
        caSystem.createFile("jiquanxi5.txt");
        caSystem.createFile("jiquanxi6.txt");
//        caSystem.createFile("jiquanxi7.txt");
//        caSystem.createFile("jiquanxi8.txt");
//        caSystem.createFile("jiquanxi9.txt");
//        caSystem.createFile("jiquanxi10.txt");
    }

    @Test
    public void testListFiles() {
        List<String> fileNameList = caSystem.getFileList();
        fileNameList.forEach(StdOut::println);
    }
}
