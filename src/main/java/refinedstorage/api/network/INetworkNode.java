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
     * Called when this node is placed in the world.
     *
     * @param world The world
     */
    void onPlaced(World world);

    /**
     * Called when this node is removed from the world.
     *
     * @param world The world
     */
    void onBreak(World world);

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
