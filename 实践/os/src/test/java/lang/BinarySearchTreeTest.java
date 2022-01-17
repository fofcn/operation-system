package lang;

import lang.tree.BinarySearchTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * 二叉搜索树测试
 *
 * @author jiquanxi
 * @date 2022/01/13
 */
public class BinarySearchTreeTest {

    private BinarySearchTree<Integer, String> bsTree = new BinarySearchTree<>();

    @Test
    public void testPut() {
        // 添加根节点
        bsTree.put(10, "10");

        // 添加到左边
        bsTree.put(9, "9");
        // 添加到右边
        bsTree.put(11, "11");

        Assert.assertEquals("10", bsTree.get(10));
        Assert.assertEquals("9", bsTree.get(9));
        Assert.assertEquals("11", bsTree.get(11));
    }

    @Test
    public void testNormalPutGet() {
        bsTree.put(10,"10");
        bsTree.put(16,"16");
        bsTree.put(4,"4");
        bsTree.put(1,"1");
        bsTree.put(12,"12");
        bsTree.put(2,"2");
        bsTree.put(0,"0");
        bsTree.put(11,"11");
        bsTree.put(18,"18");
        bsTree.put(6,"6");

        // 查找6
        Assert.assertEquals("6", bsTree.get(6));
    }

    @Test
    public void testDelete() {
        bsTree.put(10,"10");
        bsTree.put(16,"16");
        bsTree.put(4,"4");
        bsTree.put(1,"1");
        bsTree.put(12,"12");
        bsTree.put(2,"2");
        bsTree.put(0,"0");
        bsTree.put(11,"11");
        bsTree.put(18,"18");
        bsTree.put(6,"6");
        bsTree.put(5,"5");
        bsTree.put(7,"7");

        bsTree.delete(4);
        Assert.assertNull(bsTree.get(4));
    }

    @Test
    public void testGenRandomNumber() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.print(random.nextInt(20) + "  ");
        }
    }
}
