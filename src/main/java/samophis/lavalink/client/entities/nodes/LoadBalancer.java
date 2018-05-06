package samophis.lavalink.client.entities.nodes;

public interface LoadBalancer {
    AudioNode getNode();
    int getPlayerPenalty();
    int getCpuPenalty();
    int getDeficitFramePenalty();
    int getNullFramePenalty();
    int getTotalPenalty();
}