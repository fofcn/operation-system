package com.github.futurefs.netty.netty;

import com.alibaba.fastjson.JSON;
import com.github.futurefs.netty.enums.ResponseCode;
import com.github.futurefs.netty.util.NetworkSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author errorfatal89@gmail.com
 */
public class NetworkCommand {

    private static final Logger log = LoggerFactory.getLogger("Network");

    /**
     * 请求
     */
    public static final int REQUEST_TYPE = 1;
    /**
     * 响应
     */
    public static final int RESPONSE_TYPE = 2;
    /**
     * ONE WAY
     */
    public static final int ONE_WAY_TYPE = 3;

    /**
     * 消息序列号自增
     */
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger(0);

    /**
     * 消息类型
     */
    private int type;
    /**
     * 消息ID
     */
    private int sequenceId = SEQUENCE_GENERATOR.getAndIncrement();

    /**
     * 请求码
     */
    private int code;

    /**
     * 传输协议版本号
     */
    private int version;

    /**
     * 消息头
     */
    private byte[] header;

    /**
     * 消息体
     */
    private byte[] body;

    private CommandCustomHeader commandCustomHeader;

    public static NetworkCommand createRequestCommand(int code, Class<? extends CommandCustomHeader> classOfType) {
        if (classOfType != null) {
            try {
                CommandCustomHeader objectHeader = classOfType.newInstance();
                return createRequestCommand(code, objectHeader);
            } catch (InstantiationException e) {
                log.error("", e);
                return null;
            } catch (IllegalAccessException e) {
                log.error("", e);
                return null;
            }
        }

        return null;
    }

    public static NetworkCommand createRequestCommand(int code, CommandCustomHeader commandCustomHeader) {
        NetworkCommand cmd = new NetworkCommand();
        cmd.setCode(code);
        cmd.setType(REQUEST_TYPE);
        cmd.setCommandCustomHeader(commandCustomHeader);
        return cmd;
    }
    public static NetworkCommand createRequestCommand(CommandCustomHeader commandCustomHeader) {
        return createRequestCommand(commandCustomHeader.getCode(), commandCustomHeader);
    }

    public static NetworkCommand createResponseCommand(Class<? extends CommandCustomHeader> classOfType) {
        return createResponseCommand(ResponseCode.SYSTEM_ERROR.getCode(), classOfType);
    }

    public static NetworkCommand createResponseCommand(int code, CommandCustomHeader objectHeader) {
        NetworkCommand cmd = new NetworkCommand();
        cmd.setType(RESPONSE_TYPE);
        cmd.setCode(code);
        cmd.setCommandCustomHeader(objectHeader);

        return cmd;
    }

    public static NetworkCommand createResponseCommand(int code, byte[] body) {
        NetworkCommand cmd = new NetworkCommand();
        cmd.setType(RESPONSE_TYPE);
        cmd.setCode(code);
        cmd.setBody(body);
        return cmd;
    }

    public static NetworkCommand createResponseCommand(int code, Class<? extends CommandCustomHeader> classOfType) {
        NetworkCommand cmd = new NetworkCommand();
        cmd.setType(RESPONSE_TYPE);
        cmd.setCode(code);
        if (classOfType != null) {
            try {
                CommandCustomHeader objectHeader = classOfType.newInstance();
                cmd.setCommandCustomHeader(objectHeader);
            } catch (InstantiationException e) {
                log.error("", e);
                return null;
            } catch (IllegalAccessException e) {
                log.error("", e);
                return null;
            }
        }

        return cmd;
    }

    public void markResponseType() {
        this.type = RESPONSE_TYPE;
    }

    public void markRequestType() {
        this.type = REQUEST_TYPE;
    }

    public void markOnewayType() {
        this.type = ONE_WAY_TYPE;
    }

    public boolean isOnewayType() {
        return this.type == ONE_WAY_TYPE;
    }
    /**
     * 消息编码
     * @return
     */
    public ByteBuffer encode() {
        int totalLen = 20;

        int headerLen = 0;
        if (commandCustomHeader != null) {
            header = NetworkSerializable.jsonEncode(commandCustomHeader);
        }

        if (header != null) {
            headerLen = header.length;
        }

        totalLen += headerLen;

        if (body != null) {
            totalLen += body.length;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + totalLen);
        byteBuffer.putInt(totalLen);
        byteBuffer.putInt(version);
        byteBuffer.putInt(type);
        byteBuffer.putInt(sequenceId);
        byteBuffer.putInt(code);
        byteBuffer.putInt(headerLen);

        if (header != null) {
            byteBuffer.put(header);
        }

        if (body != null) {
            byteBuffer.put(body);
        }
        byteBuffer.flip();

        return byteBuffer;
    }

    /**
     * 消息解码
     * @param byteBuffer
     * @return
     */
    public static NetworkCommand decode(ByteBuffer byteBuffer) {
        int msgLen = byteBuffer.limit();
        int bodyLen = 0;

        NetworkCommand networkCommand = new NetworkCommand();
        networkCommand.setVersion(byteBuffer.getInt());
        networkCommand.setType(byteBuffer.getInt());
        networkCommand.setSequenceId(byteBuffer.getInt());
        networkCommand.setCode(byteBuffer.getInt());

        int headerLen = byteBuffer.getInt();
        if (headerLen > 0) {
            byte[] headerBytes = new byte[headerLen];
            byteBuffer.get(headerBytes);
            networkCommand.setHeader(headerBytes);
        }

        bodyLen = msgLen - headerLen - 20;

        byte[] bodyData = null;
        if (bodyLen > 0) {
            bodyData = new byte[bodyLen];
            byteBuffer.get(bodyData);
            networkCommand.setBody(bodyData);
        }

        return networkCommand;
    }

    public <T> T decodeHeader(Class<T> classType) {
        try {
            String headerStr = new String(header, "utf-8");
            return JSON.parseObject(headerStr, classType);
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }

        return null;
    }

    public static boolean isResponseOK(NetworkCommand response) {
        return response != null && response.getCode() == ResponseCode.SUCCESS.getCode();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public CommandCustomHeader getCommandCustomHeader() {
        return commandCustomHeader;
    }

    public void setCommandCustomHeader(CommandCustomHeader commandCustomHeader) {
        this.commandCustomHeader = commandCustomHeader;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        String str = "NetworkCommand{" +
                "type=" + type +
                ", sequenceId=" + sequenceId +
                ", code=" + code;

        if (commandCustomHeader != null) {
            str += ", commandCustomHeader=" + commandCustomHeader.toString() +
                    '}';
        }

        return str;
    }
}
