package refinedstorage.api.network.registry;

import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.INetworkMaster;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A registry of all Refined Storage networks on this server.
 */
public interface INetworkRegistry {
    /**
     * Adds a network to the registry.
     *
     * @param network The network
     */
    void addNetwork(@Nonnull INetworkMaster network);

    /**
     * Removes a network from the registry.
     *
     * @param pos The position of the network
     */
    void removeNetwork(@Nonnull BlockPos pos);

    /**
     * @return All the networks in this registry
     */
    Collection<INetworkMaster> getNetworks();

    /**
     * Returns a network at a position.
     *
     * @param pos The position of the network
     * @return The network
     */
    @Nullable
    INetworkMaster getNetwork(@Nonnull BlockPos pos);
}
