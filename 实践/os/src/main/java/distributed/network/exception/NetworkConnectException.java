package distributed.network.exception;

public class NetworkConnectException extends NetworkException {

    public NetworkConnectException(String addr) {
        this(addr, null);
    }

    public NetworkConnectException(String addr, Throwable cause) {
        super("connect to <" + addr + "> failed", cause);
    }
}
