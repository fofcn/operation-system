package com.github.futurefs.store.block;

import com.github.futurefs.common.R;
import com.github.futurefs.common.RWrapper;
import com.github.futurefs.common.ResultCode;
import com.github.futurefs.store.block.pubsub.BlockFileProducer;
import com.github.futurefs.store.common.AppendResult;
import com.github.futurefs.store.common.BaseFile;
import com.github.futurefs.store.common.constant.StoreConstant;
import com.github.futurefs.store.pubsub.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

/**
 * 块文件
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/23 17:33:00
 */
@Slf4j
public class BlockFile extends BaseFile {

    private static final long SUPER_BLOCK_LENGTH = 4096;

    private final SuperBlock superBlock;

    private volatile MappedByteBuffer mappedBuffer;

    private final Broker broker;

    private final BlockFileProducer producer;

    public BlockFile(File file, final Broker broker) {
        super(file);
        this.superBlock = new SuperBlock(StoreConstant.STORE_SUPER_MAGIC_NUMBER,
                StoreConstant.STORE_SUPER_MAGIC_NUMBER, 0L, 0L);
        this.broker = broker;
        this.producer = new BlockFileProducer(broker);
    }

    @Override
    protected void doInitNewFile() throws IOException {
        // 文件新建，先预分配超级块
        padFile(SUPER_BLOCK_LENGTH);
        // 写入超级块魔数
        writeLong(superBlock.getMagic());
        // 写入版本号
        writeLong(superBlock.getVersion());
        // 写入文件数量
        writeLong(superBlock.getAmount().get());
        // 重定位写入位置
        resetWritePos(SUPER_BLOCK_LENGTH);

        mappedBuffer = map(0, 4096);
    }

    @Override
    protected void doInitOldFile() throws IOException {
        log.info("block file nothing with old file for now.");
        mappedBuffer = map(0, 4096);
    }

    @Override
    protected void doAfterInit() {
        broker.registerProducer(StoreConstant.BLOCK_TOPIC_NAME, producer);
    }

    @Override
    protected void doRecover() throws IOException {
        // 读取超级块
        long magic = readLong(0);
        if (magic != superBlock.getMagic()) {
            log.error("magic number error.");
            return;
        }

        long version = readLong();
        if (version != superBlock.getVersion()) {
            log.error("version number error.");
            return;
        }

        long amount = readLong();
        superBlock.getAmount().set(amount);

        long writePos = readLong();
        superBlock.getWritePos().set(writePos);
        resetWritePos(writePos);
    }

    @Override
    protected void doAfterAppend(long offset, int length) {
        // 更新超级块信息
        superBlock.getAmount().incrementAndGet();
        superBlock.getWritePos().addAndGet(length);
        ByteBuffer byteBuffer = mappedBuffer.slice();
        byteBuffer.position(0);
        byteBuffer.put(superBlock.encode());
        try {
            flush();
        } catch (IOException e) {
            log.error("flush error");
        }
    }

    public AppendResult append(FileBlock fileBlock) {
        ByteBuffer buffer = fileBlock.encode();
        R<AppendResult> appendResult = append(buffer);
        if (RWrapper.isSuccess(appendResult)) {
            // 异步更新索引文件
            producer.produce(fileBlock.getHeader(), appendResult.getData().getOffset());
        }

        return appendResult.getData();
    }

    public FileBlock read(long pos) {
        ByteBuffer buffer = read(pos, FileBlock.HEADER_LENGTH);
        FileHeader header = FileBlock.decodeHeader(buffer);

        buffer = read(pos + FileBlock.HEADER_LENGTH, (int) (header.getLength() + Long.BYTES));
        FileBlock fileBlock = new FileBlock();
        fileBlock.setHeader(header);
        byte[] body = new byte[(int) header.getLength()];
        buffer.get(body);
        fileBlock.setBody(body);

        FileTailor fileTailor = new FileTailor();
        fileTailor.setTailorMagic(buffer.getLong());
        fileBlock.setTailor(fileTailor);
        return fileBlock;
    }
}
