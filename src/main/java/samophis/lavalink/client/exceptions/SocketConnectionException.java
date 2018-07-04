package samophis.lavalink.client.exceptions;

/**
 * Error thrown during an attempt from LavaClient to connect to an {@link samophis.lavalink.client.entities.AudioNode AudioNode}.
 *
 * @author Sam Pritchard
 * @since 2.5.0
 */

public class SocketConnectionException extends RuntimeException {
    /**
     * Captures a previously thrown Throwable and re-throws it as a SocketConnectionException.
     * @param thr A Throwable instance.
     */
    public SocketConnectionException(Throwable thr) {
        super(thr);
    }
}
