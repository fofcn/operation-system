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
 * @author errorfatal89@gmail.com
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
        caSystem.createFile("errorfatal89@gmail.com1.txt");
        caSystem.createFile("errorfatal89@gmail.com2.txt");
        caSystem.createFile("errorfatal89@gmail.com3.txt");
        caSystem.createFile("errorfatal89@gmail.com4.txt");
        caSystem.createFile("errorfatal89@gmail.com5.txt");
        caSystem.createFile("errorfatal89@gmail.com6.txt");
        caSystem.createFile("errorfatal89@gmail.com7.txt");
        caSystem.createFile("errorfatal89@gmail.com8.txt");
        caSystem.createFile("errorfatal89@gmail.com9.txt");
        caSystem.createFile("errorfatal89@gmail.com10.txt");
    }

    @Test
    public void testListFiles() {
        List<String> fileNameList = caSystem.getFileList();
        fileNameList.forEach(StdOut::println);
    }

    @Test
    public void testOpenWriteReadFile() throws FileNotFoundException {
        long inodeNumber = caSystem.openFile("errorfatal89@gmail.com8.txt");

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
