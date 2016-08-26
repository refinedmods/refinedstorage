package refinedstorage.api.network.registry;

public interface INetworkRegistryProvider {
    INetworkRegistry provide(int dimension);
}
