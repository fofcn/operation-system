package distributed.network.exception;

public class NetworkSendRequestException extends NetworkException {

    public NetworkSendRequestException(String addr) {
        this(addr, null);
    }

    public NetworkSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
