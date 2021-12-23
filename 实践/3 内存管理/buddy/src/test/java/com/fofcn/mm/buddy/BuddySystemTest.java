package com.fofcn.mm.buddy;

import com.fofcn.mm.buddy.BuddySystem;
import com.fofcn.mm.buddy.MemoryBlock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 伙伴系统测试
 *
 * @author jiquanxi
 * @date 2021/12/20
 */
public class BuddySystemTest {

    private BuddySystem buddySystem;
    private static final int MAX_MEM_SIZE = 1024 * 1024 * 1024;

    @Before
    public void before() {
        buddySystem = new BuddySystem(MAX_MEM_SIZE);
    }

    @Test
    public void testInitialize() {
        buddySystem.initialize();
        Assert.assertEquals(MAX_MEM_SIZE, buddySystem.getSize());
        Assert.assertEquals(MAX_MEM_SIZE, buddySystem.getIdleSize());
    }

    @Test
    public void testAllocAndFree() {
        buddySystem.initialize();

        // 分配100KB
        int size = 100 * 1024 * 1024;
        MemoryBlock block = buddySystem.alloc(size);
        Assert.assertNotNull(block);
        Assert.assertEquals(MAX_MEM_SIZE - block.getSize(), buddySystem.getIdleSize());
        Assert.assertEquals(buddySystem.getBlockSize(), 4);
        buddySystem.printBlocks();
        System.out.println("=================================");
        System.out.println("=================================");

        // 分配240KB
        size = 240 * 1024 * 1024;
        MemoryBlock block2 = buddySystem.alloc(size);
        Assert.assertNotNull(block2);
        Assert.assertEquals(MAX_MEM_SIZE - block.getSize() - block2.getSize(), buddySystem.getIdleSize());
        Assert.assertEquals(buddySystem.getBlockSize(), 4);
        buddySystem.printBlocks();
        System.out.println("=================================");
        System.out.println("=================================");

        // 分配64KB
        size = 64 * 1024 * 1024;
        MemoryBlock block3 = buddySystem.alloc(size);
        Assert.assertNotNull(block3);
        Assert.assertEquals(MAX_MEM_SIZE - block.getSize() - block2.getSize() - block3.getSize(), buddySystem.getIdleSize());
        Assert.assertEquals(buddySystem.getBlockSize(), 5);
        buddySystem.printBlocks();
        System.out.println("=================================");
        System.out.println("=================================");

        // 分配256KB
        size = 256 * 1024 * 1024;
        MemoryBlock block4 = buddySystem.alloc(size);
        Assert.assertNotNull(block3);
        Assert.assertEquals(MAX_MEM_SIZE - block.getSize() - block2.getSize()
                - block3.getSize() - block4.getSize(), buddySystem.getIdleSize());
        Assert.assertEquals(buddySystem.getBlockSize(), 6);
        buddySystem.printBlocks();
        System.out.println("=================================");
        System.out.println("=================================");

        // 释放240KB
        buddySystem.free(block2);
        Assert.assertEquals(buddySystem.getBlockSize(), 5);
    }
}
