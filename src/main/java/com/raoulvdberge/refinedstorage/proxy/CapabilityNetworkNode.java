package com.raoulvdberge.refinedstorage.proxy;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nonnull;

public class CapabilityNetworkNode {
    @CapabilityInject(INetworkNode.class)
    public static Capability<INetworkNode> NETWORK_NODE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(INetworkNode.class, new Capability.IStorage<INetworkNode>() {
            public NBTBase writeNBT(Capability<INetworkNode> capability, INetworkNode instance, EnumFacing side) {
                return new NBTTagCompound();
            }

            public void readNBT(Capability<INetworkNode> capability, INetworkNode instance, EnumFacing side, NBTBase base) {
                // no-op
            }
        }, () -> new INetworkNode() {
            @Override
            public int getEnergyUsage() {
                return 0;
            }

            @Override
            public BlockPos getPosition() {
                return null;
            }

            @Nonnull
            @Override
            public ItemStack getItemStack() {
                return new ItemStack((Item) null);
            }

            @Override
            public void onConnected(INetworkMaster network) {
            }

            @Override
            public void onDisconnected(INetworkMaster network) {
            }

            @Override
            public boolean canUpdate() {
                return false;
            }

            @Override
            public boolean canConduct(EnumFacing direction) {
                return false;
            }

            @Override
            public INetworkMaster getNetwork() {
                return null;
            }

            @Override
            public World getNodeWorld() {
                return null;
            }
        });

        Preconditions.checkNotNull(NETWORK_NODE_CAPABILITY, "Capability not registered");
    }
}
