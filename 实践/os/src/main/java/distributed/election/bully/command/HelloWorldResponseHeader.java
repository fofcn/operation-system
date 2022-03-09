package distributed.election.bully.command;

import distributed.network.enums.RequestCode;
import distributed.network.netty.CommandCustomHeader;

/**
 * Hello World Test!
 *
 * @author errorfatal89@gmail.com
 * @date 2022/03/09
 */
public class HelloWorldResponseHeader implements CommandCustomHeader {

    private String echo;

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }

    @Override
    public int getCode() {
        return RequestCode.ELECTION_ACK.getCode();
    }
}
