package com.github.futurefs.netty.exception;

/**
 * 网络异常
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/06 16:26
 */
public class TrickyFsNetworkException extends TrickyFsException {

    public TrickyFsNetworkException(Exception e) {
        super(e);
    }
}
