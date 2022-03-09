package distributed.network.mini;

import distributed.network.mini.function.Prediction;
import distributed.network.mini.function.PredictionTuple;
import distributed.network.mini.function.Predictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 重试策略
 *
 * @author errorfatal89@gmail.com
 */
public class RetryPolicy {
    /**
     * 重试时间间隔
     */
    private long delay;
    /**
     * 重试时间单位
     */
    private TimeUnit delayTimeUnit;
    /**
     * 最大重试次数
     */
    private int maxRetries;
    /**
     * 重试条件
     */
    private List<PredictionTuple<Object, Throwable>> retryConditions;

    public RetryPolicy() {
        this.maxRetries = 1;
        this.retryConditions = new ArrayList<>();
    }

    /**
     * 设置重试条件
     * @param failure 重试条件
     * @return this
     */
    public RetryPolicy retryOn(Class<? extends Throwable> failure) {
        return retryOn(Arrays.asList(failure));
    }

    /**
     * 设置重试条件
     * @param failures 重试条件
     * @return this
     */
    public RetryPolicy retryOn(Class<? extends Throwable>... failures) {
        return retryOn(Arrays.asList(failures));
    }

    /**
     * 设置重试条件
     * @param failures 重试条件
     * @return this
     */
    public RetryPolicy retryOn(List<Class<? extends Throwable>> failures) {
        retryConditions.add(Predictions.failurePredicateFor(failures));
        return this;
    }

    /**
     * 设置重试条件，根据返回结果判断
     * @param resultPredicate 结果阈值
     * @param <T> 结果类型
     * @return this
     */
    public <T> RetryPolicy retryIf(Prediction<T> resultPredicate) {
        retryConditions.add(Predictions.resultPredicateFor(resultPredicate));
        return this;
    }

    /**
     * 设置最大重试次数
     * @param maxRetries 最大重试次数
     * @return this
     */
    public RetryPolicy withMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    /**
     * 设置重试时间间隔
     * @param delay 重试间隔
     * @param timeUnit 重试时间单位
     * @return this
     */
    public RetryPolicy withRetryDelay(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.delayTimeUnit = timeUnit;
        return this;
    }

    public long getDelay() {
        return delay;
    }

    public TimeUnit getDelayTimeUnit() {
        return delayTimeUnit;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public List<PredictionTuple<Object, Throwable>> getRetryConditions() {
        return retryConditions;
    }

    /**
     * 是否需要重试
     * @param result 执行结果
     * @param failure 失败原因
     * @return true: 继续，否不继续
     */
    public boolean canRetryFor(Object result, Throwable failure) {
        for (PredictionTuple<Object, Throwable> predicte : retryConditions) {
            if (predicte.test(result, failure)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "RetryPolicy{" +
                "delay="
                + delay +
                ", delayTimeUnit="
                + delayTimeUnit +
                ", maxRetries="
                + maxRetries +
                ", retryConditions="
                + retryConditions +
                '}';
    }
}
