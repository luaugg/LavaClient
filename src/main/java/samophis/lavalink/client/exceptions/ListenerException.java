package samophis.lavalink.client.exceptions;

public class ListenerException extends RuntimeException {
    public ListenerException(Exception exc) {
        super(exc);
    }
}
