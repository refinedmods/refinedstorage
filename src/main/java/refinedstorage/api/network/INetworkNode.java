package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Represents a node in the storage network.
 */
public interface INetworkNode {
    /**
     * Called every tile entity tick.
     */
    void updateNode();

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
     *
     * @param network The network
     */
    void onDisconnected(INetworkMaster network);

    /**
     * Called when the connection state of this node changes.
     * This is also called when redstone mode is updated, as opposed to {@link INetworkNode#onConnected(INetworkMaster)} and {@link INetworkNode#onDisconnected(INetworkMaster)}.
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
     * @return If {@link INetworkNode#updateNode()} can get called, typically checks for connection status and redstone mode
     */
    boolean canUpdate();

    /**
     * @return Whether this node can conduct a network signal
     */
    boolean canConduct();

    /**
     * @return The network
     */
    INetworkMaster getNetwork();

    /**
     * @return The world where this node is in
     */
    World getNodeWorld();
}
