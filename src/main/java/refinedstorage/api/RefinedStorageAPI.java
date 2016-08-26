package refinedstorage.api;

import net.minecraft.world.World;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.registry.INetworkRegistry;
import refinedstorage.api.network.registry.INetworkRegistryProvider;
import refinedstorage.api.solderer.ISoldererRegistry;

import java.util.HashMap;
import java.util.Map;

public final class RefinedStorageAPI {
    /**
     * The solderer registry, set in pre-initialization
     */
    public static ISoldererRegistry SOLDERER_REGISTRY;

    /**
     * The network registry provider, set in pre-initialization
     */
    public static INetworkRegistryProvider NETWORK_REGISTRY_PROVIDER;

    private static final Map<Integer, INetworkRegistry> NETWORK_REGISTRY = new HashMap<>();

    public static INetworkRegistry getNetworkRegistry(World world) {
        return getNetworkRegistry(world.provider.getDimension());
    }

    public static void removeNetworkRegistry(World world) {
        NETWORK_REGISTRY.remove(world.provider.getDimension());
    }

    public static INetworkRegistry getNetworkRegistry(int dimension) {
        if (!NETWORK_REGISTRY.containsKey(dimension)) {
            NETWORK_REGISTRY.put(dimension, NETWORK_REGISTRY_PROVIDER.provide(dimension));
        }

        return NETWORK_REGISTRY.get(dimension);
    }

    public static INetworkMaster getNetwork(INetworkNode node) {
        for (INetworkRegistry registry : NETWORK_REGISTRY.values()) {
            for (INetworkMaster network : registry.getNetworks()) {
                for (INetworkNode otherNode : network.getNodeGraph().all()) {
                    if (node.equals(otherNode)) {
                        return network;
                    }
                }
            }
        }

        return null;
    }
}
