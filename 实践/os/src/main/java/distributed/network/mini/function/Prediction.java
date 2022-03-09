package distributed.network.mini.function;

/**
 * 结果预测
 * @param <T> 任意类型
 * @author errorfatal89@gmail.com
 */
public interface Prediction<T> {
    /**
     * 根据用户类型判定是否需要进行重试
     * @param t 结果
     * @return true:需要重试，false：不需要重试
     */
    boolean test(T t);
}
