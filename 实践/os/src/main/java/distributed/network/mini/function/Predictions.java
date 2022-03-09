package distributed.network.mini.function;

import java.util.List;

/**
 * 结果预测工具类
 * @author errorfatal89@gmail.com
 */
public final class Predictions {

    private Predictions() {

    }

    /**
     * 创建结果预测
     * @param failures 异常列表
     * @return 预测类实例
     */
    public static PredictionTuple<Object, Throwable> failurePredicateFor(
            final List<Class<? extends Throwable>> failures) {
        return new PredictionTuple<Object, Throwable>() {
            @Override
            public boolean test(Object t, Throwable u) {
                if (u == null) {
                    return false;
                }
                for (Class<? extends Throwable> failureType : failures) {
                    if (failureType.isAssignableFrom(u.getClass())) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * 创建结果预测
     * @param resultPredicate 预测类实例
     * @param <T> 结果类型
     * @return 预测类实例
     */
    public static  <T> PredictionTuple<Object, Throwable> resultPredicateFor(final Prediction<T> resultPredicate) {
        return new PredictionTuple<Object, Throwable>() {
            @Override
            public boolean test(Object t, Throwable u) {
                if (u == null) {
                    return ((Prediction<Object>) resultPredicate).test(t);
                } else {
                    return false;
                }
            }
        };
    }
}
