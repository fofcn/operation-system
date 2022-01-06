package fs.trivial;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.StdOut;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
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
        caSystem.createFile("jiquanxi7.txt");
        caSystem.createFile("jiquanxi8.txt");
        caSystem.createFile("jiquanxi9.txt");
        caSystem.createFile("jiquanxi10.txt");
    }

    @Test
    public void testListFiles() {
        List<String> fileNameList = caSystem.getFileList();
        fileNameList.forEach(StdOut::println);
    }

    @Test
    public void testOpenWriteReadFile() throws FileNotFoundException {
        long inodeNumber = caSystem.openFile("jiquanxi8.txt");

        String writeStr = "Hello world!";
        try {
            caSystem.writeFile(inodeNumber, writeStr.getBytes(StandardCharsets.UTF_8));
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
        }

        byte[] fileContent = caSystem.readFile(inodeNumber);
        String readStr = new String(fileContent, StandardCharsets.UTF_8);
        StdOut.println(readStr);
        Assert.assertEquals(writeStr, readStr);
    }
}
