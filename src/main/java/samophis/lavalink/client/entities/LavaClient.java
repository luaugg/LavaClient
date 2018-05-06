package samophis.lavalink.client.entities;

import samophis.lavalink.client.entities.internal.LavaClientImpl;
import samophis.lavalink.client.entities.internal.LoadBalancerImpl;
import samophis.lavalink.client.entities.nodes.AudioNode;

import java.util.List;

public interface LavaClient {
    int REST_PORT_DEFAULT = 2333;
    int WS_PORT_DEFAULT = 80;
    String PASSWORD_DEFAULT = "youshallnotpass";
    List<LavaPlayer> getPlayers();
    List<AudioNode> getAudioNodes();
    LavaHttpManager getHttpManager();
    AudioNode getNodeByIdentifier(String address, int websocketPort);
    LavaPlayer getPlayerByGuildId(long guild_id);
    String getGlobalServerPassword();
    int getGlobalWebSocketPort();
    int getGlobalRestPort();
    int getShardCount();
    long getUserId();
    void addEntry(AudioNodeEntry entry);
    static AudioNode getBestNode() {
        AudioNode node = null;
        int record = Integer.MAX_VALUE;
        for (AudioNode nd : LavaClientImpl.NODES.values()) {
            int penalty = ((LoadBalancerImpl) nd.getBalancer()).initWithNode().getTotalPenalty();
            if (penalty < record) {
                node = nd;
                record = penalty;
            }
        }
        if (node == null)
            throw new IllegalStateException("No available Lavalink nodes!");
        if (!node.isAvailable())
            throw new IllegalStateException("Lavalink node isn't open/connected!");
        return node;
    }
}
