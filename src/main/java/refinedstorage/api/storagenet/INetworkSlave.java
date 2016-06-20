package refinedstorage.api.storagenet;

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
     * @return The energy usage of this slave
     */
    int getEnergyUsage();

    /**
     * @return If this slave can send connectivity updates and can trigger block updates
     */
    boolean canSendConnectivityUpdate();

    /**
     * @return The position of this slave in the world
     */
    BlockPos getPosition();

    /**
     * Called when the neighbor of this slave is changed. Typically used to recalculate the network (if there is still a connection)
     *
     * @param world The world
     */
    void onNeighborChanged(World world);

    /**
     * Called when a connection is found to the storage network
     *
     * @param world   The world
     * @param network The network we're trying to connect to
     */
    void connect(World world, NetworkMaster network);

    /**
     * Called when the slave is loaded from a save file
     *
     * @param network The network we have to connected to
     */
    void forceConnect(NetworkMaster network);

    /**
     * Called when a connection is lost to the stoarge network
     *
     * @param world The world
     */
    void disconnect(World world);

    /**
     * @return If we are connected
     */
    boolean isConnected();

    /**
     * @return If this slave can be updated. Typically returns false when redstone mode doesn't allow it.
     */
    boolean canUpdate();

    /**
     * @return The network
     */
    NetworkMaster getNetwork();
}
