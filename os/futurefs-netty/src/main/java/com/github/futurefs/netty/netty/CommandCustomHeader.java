package com.github.futurefs.netty.netty;

import java.io.Serializable;

/**
 * @author errorfatal89@gmail.com
 */
public interface CommandCustomHeader extends Serializable {
    int getCode();
}
