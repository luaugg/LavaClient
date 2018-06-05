package samophis.lavalink.client.entities;

import com.neovisionaries.ws.client.WebSocket;
import javax.annotation.Nonnull;

/**
 * Represents an initializer that allows users to modify the raw WebSocket instance before LavaClient opens a connection to it.
 * <br><p>Any node created with this initializer will maintain its set properties even after being re-created.
 * Additionally, it is recommended to use the Java 8 Lambda features for code cleanliness. If you don't know how to use them LavaClient probably isn't the library for you.</p>
 *
 * @since 1.2
 * @author SamOphis
 */

@FunctionalInterface
public interface SocketInitializer {
    /**
     * The actual method used to initialize a pre-created WebSocket before a connection is made with it.
     * @param socket A WebSocket instance.
     * @return A <b>not-null</b> WebSocket instance with the updated properties.
     */
    @Nonnull WebSocket initialize(WebSocket socket);
}