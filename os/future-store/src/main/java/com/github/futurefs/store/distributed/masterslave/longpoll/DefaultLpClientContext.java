package com.github.futurefs.store.distributed.masterslave.longpoll;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO 文件说明
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/04/04 00:02
 */
public class DefaultLpClientContext implements LongPollClientContext {

    private final Map map = new HashMap();

    @Override
    public void addAttr(Object key, Object value) {
        map.put(key, value);
    }

    @Override
    public Object getAttr(Object key) {
        return map.get(key);
    }
}
