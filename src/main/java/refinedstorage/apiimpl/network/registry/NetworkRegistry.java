package refinedstorage.apiimpl.network.registry;

import net.minecraft.util.math.BlockPos;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.registry.INetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkRegistry implements INetworkRegistry {
    private Map<BlockPos, INetworkMaster> networks = new HashMap<>();

    @Override
    public void addNetwork(@Nonnull INetworkMaster network) {
        networks.put(network.getPosition(), network);
    }

    @Override
    public void removeNetwork(@Nonnull BlockPos pos) {
        networks.remove(pos);
    }

    @Override
    public Collection<INetworkMaster> getNetworks() {
        return networks.values();
    }

    @Nullable
    @Override
    public INetworkMaster getNetwork(@Nonnull BlockPos pos) {
        return networks.get(pos);
    }
}
