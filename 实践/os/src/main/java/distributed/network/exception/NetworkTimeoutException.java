package distributed.network.exception;

public class NetworkTimeoutException extends NetworkException {

    public NetworkTimeoutException(String message) {
        super(message);
    }

    public NetworkTimeoutException(String addr, long timeoutMillis) {
        this(addr, timeoutMillis, null);
    }

    public NetworkTimeoutException(String addr, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + addr + "> timeout, " + timeoutMillis + "(ms)", cause);
    }
}
