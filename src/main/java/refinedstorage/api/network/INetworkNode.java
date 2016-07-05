package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;

/**
 * Represents a node in the storage network.
 */
public interface INetworkNode {
    /**
     * Called every tile entity tick.
     */
    void updateNode();

    /**
     * @return If this node can send a connectivity update
     */
    boolean canSendConnectivityUpdate();

    /**
     * @return The energy usage of this node
     */
    int getEnergyUsage();

    /**
     * @return The position of this node in the world
     */
    BlockPos getPosition();

    /**
     * Called when this node is connected to a network.
     *
     * @param network The network
     */
    void onConnected(INetworkMaster network);

    /**
     * Called when this node is disconnected from a network.
     */
    void onDisconnected();

    /**
     * Called when the connection state of this node changes.
     * This is also called when redstone mode is updated, as opposed to {@link INetworkNode#onConnected(INetworkMaster)} and {@link INetworkNode#onDisconnected()}.
     *
     * @param network The network
     * @param state   The state
     */
    void onConnectionChange(INetworkMaster network, boolean state);

    /**
     * @return If we are connected
     */
    boolean isConnected();

    /**
     * @return If {@link INetworkNode#canUpdate()} can get called. Typically checks for connection status and redstone mode.
     */
    boolean canUpdate();

    /**
     * @return The network
     */
    INetworkMaster getNetwork();
}
