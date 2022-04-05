package com.github.futurefs.netty.exception;

/**
 * TrickyFsException
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/02 16:31
 */
public class TrickyFsException extends Exception {
    public TrickyFsException(Exception e) {
        super(e);
    }

    public TrickyFsException(String msg) {
        super(msg);
    }
}
