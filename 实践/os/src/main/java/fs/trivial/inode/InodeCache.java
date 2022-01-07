package fs.trivial.inode;

import cache.Cache;
import cache.lru.LruCache;

/**
 * i-node节点缓存
 *
 * @author jiquanxi
 * @date 2022/01/06
 */
public class InodeCache {

    /**
     * 最大缓存个数
     */
    private final int maxCacheSize = 100;

    /**
     * i-node缓存map
     */
    private final Cache<Long, Inode> inodeCache;

    public InodeCache() {
        inodeCache = new LruCache<>(maxCacheSize);
    }

    public boolean initialize() {
        return true;
    }

    public Inode get(long inodeNumber) {
        return inodeCache.get(inodeNumber);
    }

    /**
     * 设置一个缓存
     * @param inode i-node节点信息
     */
    public void set(Inode inode) {
        inodeCache.set(inode.getNumber(), inode);
    }
}
