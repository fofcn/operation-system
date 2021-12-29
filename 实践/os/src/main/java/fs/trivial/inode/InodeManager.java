package fs.trivial.inode;

import fs.trivial.Manager;
import lang.serializer.ByteArraySerializer;

/**
 * I-node manager of file system.
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class InodeManager implements Manager {

    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public int getIndexNodeSize() {
        INode iNode = new INode();
        byte[] iNodeBytes = ByteArraySerializer.serialize(iNode, INode.class);
        return iNodeBytes.length;
    }
}
