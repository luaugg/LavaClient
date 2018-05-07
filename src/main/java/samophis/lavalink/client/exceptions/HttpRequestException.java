package samophis.lavalink.client.exceptions;

/**
 * Represents an exception thrown by LavaClient caused by an issue with a HTTP Request to the Lavalink Server.
 *
 * @author SamOphis
 * @since 0.2
 */

@SuppressWarnings("unused")
public class HttpRequestException extends RuntimeException {
    public HttpRequestException(Exception exception) {
        super(exception);
    }
    public HttpRequestException(String message) {
        super(message);
    }
}
