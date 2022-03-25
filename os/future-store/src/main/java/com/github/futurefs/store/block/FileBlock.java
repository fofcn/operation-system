package com.github.futurefs.store.block;

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
public class FileBlock {

    public static final int HEADER_LENGTH = 5 * Long.BYTES;

    private FileHeader header;

    private byte[] body;

    private FileTailor tailor;

    public ByteBuffer encode() {
        Checksum crc64 = new CRC32();
        crc64.update(body, 0, body.length);
        int fileSize = 6 * Long.BYTES + body.length;
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

    public static FileHeader decodeHeader(ByteBuffer buffer) {
        FileHeader header = new FileHeader();
        header.setHeaderMagic(buffer.getLong());
        header.setDeleteStatus(buffer.getLong());
        header.setCrc64Number(buffer.getLong());
        header.setKey(buffer.getLong());
        header.setLength(buffer.getLong());
        return header;
    }
}
