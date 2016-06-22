package refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Represents a slave or machine in the storage network.
 */
public interface INetworkSlave {
    /**
     * Called every server tick.
     */
    void updateSlave();

    /**
     * @return If the slave can send a connectivity update (for most slaves this is true, for cables it's false)
     */
    boolean canSendConnectivityUpdate();

    /**
     * @return The energy usage of this slave
     */
    int getEnergyUsage();

    /**
     * @return The position of this slave in the world
     */
    BlockPos getPosition();

    /**
     * Called when the neighbor of this slave is changed. Typically used to check if there is still a connection to the network.
     *
     * @param world The world
     */
    void onNeighborChanged(World world);

    /**
     * Called when a connection is found to the network
     *  @param world   The world
     * @param network The network we're trying to connect to
     */
    void connect(World world, INetworkMaster network);

    /**
     * Called when the slave is loaded from a save file
     *
     * @param network The network we have to connected to
     */
    void forceConnect(INetworkMaster network);

    /**
     * Called when a connection is lost to the network
     *
     * @param world The world
     */
    void disconnect(World world);

    /**
     * @return If we are connected
     */
    boolean isConnected();

    /**
     * @return If {@link INetworkSlave#canUpdate()} can get called. Typically checks for connection and redstone mode.
     */
    boolean canUpdate();

    /**
     * @return The network
     */
    INetworkMaster getNetwork();
}
