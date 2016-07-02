package refinedstorage.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import refinedstorage.api.network.INetworkNode;

public final class RefinedStorageCapabilities {
    @CapabilityInject(INetworkNode.class)
    public static final Capability<INetworkNode> NETWORK_NODE_CAPABILITY = null;
}
