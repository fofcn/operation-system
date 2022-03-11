package distributed.election.bully.command;

import distributed.network.enums.RequestCode;
import distributed.network.netty.CommandCustomHeader;
import lombok.Data;

/**
 * 心跳消息响应头
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
@Data
public class HeartBeatResponseHeader implements CommandCustomHeader {

    /**
     * 标识符
     */
    private int identifier;

    /**
     * 回答
     */
    private String remark = "我很好，谢谢！";

    @Override
    public int getCode() {
        return RequestCode.HEART_BEAT.getCode();
    }
}
