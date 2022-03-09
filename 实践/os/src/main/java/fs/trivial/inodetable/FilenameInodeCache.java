package fs.trivial.inodetable;

import cache.Cache;
import cache.lru.LruCache;

/**
 * file name 与 i-node节点缓存
 *
 * @author errorfatal89@gmail.com
 * @date 2022/01/06
 */
public class FilenameInodeCache {

    /**
     * 最大缓存个数
     */
    private final int maxCacheSize = 100;

    /**
     * i-node缓存map
     */
    private final Cache<String, FileNameInode> inodeCache;

    public FilenameInodeCache() {
        inodeCache = new LruCache<>(maxCacheSize);
    }

    public boolean initialize() {
        return true;
    }

    public FileNameInode get(String name) {
        return inodeCache.get(name);
    }

    /**
     * 设置一个缓存
     * @param fileNameInode file name i-node节点信息
     */
    public void set(FileNameInode fileNameInode) {
        inodeCache.set(fileNameInode.getName(), fileNameInode);
    }
}
