package retry;

/**
 * 重试代理
 *
 * 算法流程
 * 1. 启动扫描包下所有的方法注解
 * 2. 检查是否有@Retry注解
 * 3. 如果没有@Retry注解则直接执行
 * 4. 如果有@Retry注解，则监控返回值或异常信息，如果有异常或返回值在Predicate为true，则执行Retry逻辑
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/14
 */
public class RetryProxy {

}
