
package com.github.futurefs.netty.mini.function;

/**
 * 结果预测
 * @param <T> 任意类型
 * @param <U> 任意类型
 * @author errorfatal89@gmail.com
 */
public interface PredictionTuple<T, U> {
  /**
   * 根据用户类型判定是否需要进行重试
   * @param t 结果
   * @param u 一般为异常
   * @return true:需要重试，false：不需要重试
   */
  boolean test(T t, U u);
}
