package samophis.lavalink.client.exceptions;

/**
 * Represents an exception thrown within an {@link samophis.lavalink.client.entities.events.AudioEventListener AudioEventListener}.
 *
 * @author SamOphis
 * @since 0.1
 */

public class ListenerException extends RuntimeException {
    public ListenerException(Exception exc) {
        super(exc);
    }
}
