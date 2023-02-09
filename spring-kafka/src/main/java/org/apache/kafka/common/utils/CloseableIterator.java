package org.apache.kafka.common.utils;

import java.util.Iterator;

/**
 * @Author: Eric
 * @Email: eyes.left@qq.com
 * @Date: 2023/2/9 10:15
 */
public interface CloseableIterator<T> {

    void close();

    CloseableIterator<T> wrap(Iterator<T> iterator);

}
