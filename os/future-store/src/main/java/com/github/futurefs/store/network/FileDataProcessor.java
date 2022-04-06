package com.github.futurefs.store.network;

import com.github.futurefs.netty.FileDataProtos;
import com.github.futurefs.netty.FileDataProtos.FileReply;
import com.github.futurefs.netty.NettyProtos.NettyReply ;
import com.github.futurefs.netty.NettyProtos.NettyRequest;
import com.github.futurefs.netty.enums.ResponseCode;
import com.github.futurefs.netty.netty.NetworkCommand;
import com.github.futurefs.netty.processor.NettyRequestProcessor;
import com.github.futurefs.store.block.BlockFile;
import com.github.futurefs.store.block.FileBlock;
import com.github.futurefs.store.block.FileHeader;
import com.github.futurefs.store.block.FileTailor;
import com.github.futurefs.store.common.AppendResult;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 文件数据处理器
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/28 17:08
 */
@Slf4j
public class FileDataProcessor implements NettyRequestProcessor {

    private final BlockFile fileData;

    public FileDataProcessor(BlockFile fileData) {
        this.fileData = fileData;
    }

    @Override
    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
        NettyRequest body = NettyRequest.parseFrom(request.getBody());
        if (NettyRequest.RequestCase.FILEREQUEST.equals(body.getRequestCase())) {
            log.info("file upload request..");
        }

        // 组装File对象
        FileHeader header = new FileHeader();
        header.setDeleteStatus(0);
        header.setKey(new Random().nextLong());
        header.setLength(body.getFileRequest().getLength());
        FileTailor tailor = new FileTailor();
        FileBlock fileBlock = new FileBlock();
        fileBlock.setHeader(header);
        fileBlock.setBody(body.getFileRequest().getData().toByteArray());
        fileBlock.setTailor(tailor);
        AppendResult result = fileData.append(fileBlock);
        FileReply fileReply;
        if (result.getOffset() != -1L) {
            fileReply = FileReply.newBuilder()
                    .setSuccess(true)
                    .setKey(header.getKey())
                    .build();
        } else {
            fileReply = FileReply.newBuilder()
                    .setSuccess(false)
                    .build();
        }

        NettyReply reply = NettyReply.newBuilder().setFileReply(fileReply).build();
        return NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), reply.toByteArray());
    }
}
