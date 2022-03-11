package distributed.election.bully.command;

import distributed.network.enums.RequestCode;
import distributed.network.netty.CommandCustomHeader;
import lombok.Data;

/**
 * 心跳消息请求头
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/11
 */
@Data
public class HeartBeatRequestHeader implements CommandCustomHeader {

    /**
     * 标识符
     */
    private int identifier;

    /**
     * 问候
     */
    private String remark = "老大，你咋样？";

    @Override
    public int getCode() {
        return RequestCode.HEART_BEAT.getCode();
    }
}
