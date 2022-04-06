//package com.github.futurefs.store.network;
//
//import com.github.futurefs.netty.OffsetProtos.WriteOffsetRequest;
//import com.github.futurefs.netty.OffsetProtos.WriteOffsetReply;
//import com.github.futurefs.netty.enums.ResponseCode;
//import com.github.futurefs.netty.netty.NetworkCommand;
//import com.github.futurefs.netty.processor.NettyRequestProcessor;
//import com.github.futurefs.store.block.FileBlock;
//import io.netty.channel.ChannelHandlerContext;
//
///**
// * 文件偏移处理器
// *
// * @author errorfatal89@gmail.com
// * @datetime 2022/03/30 16:19
// */
//public class FileOffsetProcessor implements NettyRequestProcessor {
//
//    private final PreAllocOffset preAllocOffset;
//
//    public FileOffsetProcessor(PreAllocOffset preAllocOffset) {
//        this.preAllocOffset = preAllocOffset;
//    }
//
//    @Override
//    public NetworkCommand processRequest(ChannelHandlerContext ctx, NetworkCommand request) throws Exception {
//        WriteOffsetRequest req = WriteOffsetRequest.parseFrom(request.getBody());
//        int length = FileBlock.calcAlignLen(req.getLength());
//        // todo 判断自己是否为LEADER，如果是LEADER则响应，响应为-1L
//        long offset = preAllocOffset.alloc(length);
//        WriteOffsetReply reply;
//        if (offset == -1L) {
//            reply = WriteOffsetReply.newBuilder().setSuccess(false).setOffset(offset).build();
//        } else {
//            reply = WriteOffsetReply.newBuilder().setSuccess(true).setOffset(offset).build();
//        }
//
//        // 封装返回
//        return NetworkCommand.createResponseCommand(ResponseCode.SUCCESS.getCode(), reply.toByteArray());
//    }
//}
