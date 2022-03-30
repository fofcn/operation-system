package com.github.futurefs.store.block;

import com.github.futurefs.store.common.Codec;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * 文件
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/23
 */
@Data
public class FileBlock implements Codec<FileBlock> {

    public static final int HEADER_LENGTH = 5 * Long.BYTES;

    public static final int TAILOR_LENGTH = Long.BYTES;

    private FileHeader header;

    private byte[] body;

    private FileTailor tailor;

    @Override
    public ByteBuffer encode() {
        Checksum crc64 = new CRC32();
        crc64.update(body, 0, body.length);
        int fileSize = 6 * Long.BYTES + body.length;
        int padding = fileSize % 8;
        if (padding != 0) {
            fileSize += (Long.BYTES - padding);
        }

        ByteBuffer buffer = ByteBuffer.allocate(fileSize);
        buffer.putLong(header.getHeaderMagic());
        buffer.putLong(header.getDeleteStatus());
        buffer.putLong(crc64.getValue());
        buffer.putLong(header.getLength());
        buffer.putLong(body.length);
        buffer.put(body);
        buffer.putLong(tailor.getTailorMagic());
        buffer.flip();
        return buffer;
    }

    @Override
    public FileBlock decode(ByteBuffer buffer) {
        return null;
    }

    public static FileHeader decodeHeader(ByteBuffer buffer) {
        FileHeader header = new FileHeader();
        header.setHeaderMagic(buffer.getLong());
        header.setDeleteStatus(buffer.getLong());
        header.setCrc64Number(buffer.getLong());
        header.setKey(buffer.getLong());
        header.setLength(buffer.getLong());
        return header;
    }

    public static int calcAlignLen(int bodyLen) {
        int fileLen = 6 * Long.BYTES + bodyLen;
        int padding = fileLen % 8;
        if (padding != 0) {
            fileLen += (Long.BYTES - padding);
        }

        return fileLen;
    }
}
