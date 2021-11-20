package com.refinedmods.refinedstorage.capability;

import com.google.common.base.Preconditions;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class NetworkNodeProxyCapability {
    @CapabilityInject(INetworkNodeProxy.class)
    public static final Capability<INetworkNodeProxy> NETWORK_NODE_PROXY_CAPABILITY = null;

    private NetworkNodeProxyCapability() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(INetworkNodeProxy.class, new Storage(), new Factory());

        Preconditions.checkNotNull(NETWORK_NODE_PROXY_CAPABILITY, "Capability not registered");
    }

    private static class Storage implements Capability.IStorage<INetworkNodeProxy> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<INetworkNodeProxy> capability, INetworkNodeProxy instance, Direction side) {
            return null;
        }

        @Override
        public void readNBT(Capability<INetworkNodeProxy> capability, INetworkNodeProxy instance, Direction side, INBT nbt) {
            // NO OP
        }
    }

    private static class Factory implements Callable<INetworkNodeProxy> {
        @Override
        public INetworkNodeProxy call() {
            return new INetworkNodeProxy() {
                @Nonnull
                @Override
                public INetworkNode getNode() {
                    throw new UnsupportedOperationException("Cannot use default implementation");
                }
            };
        }
    }
}
