package refinedstorage.apiimpl.network.registry;

import refinedstorage.api.network.registry.INetworkRegistry;
import refinedstorage.api.network.registry.INetworkRegistryProvider;

public class NetworkRegistryProvider implements INetworkRegistryProvider {
    @Override
    public INetworkRegistry provide(int dimension) {
        return new NetworkRegistry();
    }
}
