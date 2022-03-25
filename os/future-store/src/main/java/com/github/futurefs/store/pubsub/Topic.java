package com.github.futurefs.store.pubsub;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 主体
 *
 * @author errorfatal89@gmail.com
 * @datetime 2022/03/25 16:00
 */
@Data
@AllArgsConstructor
public class Topic {

    /**
     * 主题名称
     */
    private String name;

}
