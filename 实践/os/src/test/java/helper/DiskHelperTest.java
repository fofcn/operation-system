package helper;

import fs.helper.DiskHelper;
import org.junit.Test;
import util.StdOut;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test for DiskHelper
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
public class DiskHelperTest {

    @Test
    public void testCreateReadWrite() throws IOException {
        DiskHelper diskHelper = new DiskHelper("test", 1024L * 1024);
        String test = "abcd";
        diskHelper.write(test.getBytes(StandardCharsets.UTF_8), 0L);

        byte[] content = diskHelper.read(0, 4);
        StdOut.println(new String(content, StandardCharsets.UTF_8));
        diskHelper.shutdown();
    }
}
