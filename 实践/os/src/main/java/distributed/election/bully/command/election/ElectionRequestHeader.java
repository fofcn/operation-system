package distributed.election.bully.command.election;

import distributed.network.enums.RequestCode;
import distributed.network.netty.CommandCustomHeader;

/**
 * 选举消息头
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/10
 */
public class ElectionRequestHeader implements CommandCustomHeader {
    /**
     * 选举标识符
     */
    private int identifier;

    /**
     * 发起选举原因
     * 1. 刚启动选举
     * 2. coordinator故障选举
     * 3. 霸道选举（我选举标识符大)
     */
    private int electionReason;

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getElectionReason() {
        return electionReason;
    }

    public void setElectionReason(int electionReason) {
        this.electionReason = electionReason;
    }

    @Override
    public int getCode() {
        return RequestCode.ELECTION.getCode();
    }
}
