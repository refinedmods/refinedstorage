package com.refinedmods.refinedstorage.capability;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class NetworkNodeProxyCapability {
    public static Capability<INetworkNodeProxy> NETWORK_NODE_PROXY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private NetworkNodeProxyCapability() {
    }
}
