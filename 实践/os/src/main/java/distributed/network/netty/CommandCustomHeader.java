package distributed.network.netty;

import java.io.Serializable;

/**
 * @author errorfatal89@gmail.com
 */
public interface CommandCustomHeader extends Serializable {
    int getCode();
}
