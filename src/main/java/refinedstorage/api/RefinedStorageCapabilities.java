package refinedstorage.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import refinedstorage.api.storagenet.INetworkSlave;

public final class RefinedStorageCapabilities {
    @CapabilityInject(INetworkSlave.class)
    public static final Capability<INetworkSlave> NETWORK_SLAVE_CAPABILITY = null;
}
