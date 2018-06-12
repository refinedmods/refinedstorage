package com.raoulvdberge.refinedstorage.capability;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class CapabilityNetworkNodeProxy {
    @CapabilityInject(INetworkNodeProxy.class)
    public static Capability<INetworkNodeProxy> NETWORK_NODE_PROXY_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(INetworkNodeProxy.class, new Storage(), new Factory());

        Preconditions.checkNotNull(NETWORK_NODE_PROXY_CAPABILITY, "Capability not registered");
    }

    private static class Storage implements Capability.IStorage<INetworkNodeProxy> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<INetworkNodeProxy> capability, INetworkNodeProxy instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<INetworkNodeProxy> capability, INetworkNodeProxy instance, EnumFacing side, NBTBase nbt) {
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
