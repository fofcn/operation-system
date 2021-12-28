package lang;

/**
 * name value pair
 *
 * @author jiquanxi
 * @date 2021/12/28
 */
public class NameValuePair<N, V> {
    private final N order;

    private final V field;

    public NameValuePair(final N order, final V field) {
        this.order = order;
        this.field = field;
    }

    public N getOrder() {
        return order;
    }

    public V getField() {
        return field;
    }
}
