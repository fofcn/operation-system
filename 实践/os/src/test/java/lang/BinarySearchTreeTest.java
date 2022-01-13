package lang;

import lang.tree.BinarySearchTree;
import org.junit.Assert;
import org.junit.Test;

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
}
