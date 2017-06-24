package com.raoulvdberge.refinedstorage.capability;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nonnull;

public class CapabilityNetworkNodeProxy {
    @CapabilityInject(INetworkNodeProxy.class)
    public static Capability<INetworkNodeProxy> NETWORK_NODE_PROXY_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(INetworkNodeProxy.class, new Capability.IStorage<INetworkNodeProxy>() {
            public NBTBase writeNBT(Capability<INetworkNodeProxy> capability, INetworkNodeProxy instance, EnumFacing side) {
                return new NBTTagCompound();
            }

            public void readNBT(Capability<INetworkNodeProxy> capability, INetworkNodeProxy instance, EnumFacing side, NBTBase base) {
                // NO OP
            }
        }, () -> new INetworkNodeProxy() {
            @Override
            @Nonnull
            public INetworkNode getNode() {
                return null;
            }
        });

        Preconditions.checkNotNull(NETWORK_NODE_PROXY_CAPABILITY, "Capability not registered");
    }
}
