package distributed.election.bully.command;

import distributed.network.enums.RequestCode;
import distributed.network.netty.CommandCustomHeader;

/**
 * 协调者消息
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
public class CoordinatorRequestHeader implements CommandCustomHeader {

    /**
     * 标识符
     */
    private int identifier;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getCode() {
        return RequestCode.COORDINATOR.getCode();
    }
}
