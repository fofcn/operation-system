package distributed.election.bully.command.election;

import distributed.network.netty.CommandCustomHeader;
import lombok.Data;

/**
 * 选举响应头
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
@Data
public class ElectionResponseHeader implements CommandCustomHeader {
    /**
     * 响应节点选举标识符
     */
    private int identifier;

    @Override
    public int getCode() {
        return -1;
    }
}
