package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NetworkNodeDummy implements INetworkNode {
    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void onConnected(INetwork network) {
        // NO OP
    }

    @Override
    public void onDisconnected(INetwork network) {
        // NO OP
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Nullable
    @Override
    public INetwork getNetwork() {
        return null;
    }

    @Override
    public void update() {
        // NO OP
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public BlockPos getPos() {
        return BlockPos.ORIGIN;
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public void markDirty() {
        // NO OP
    }

    @Override
    public String getId() {
        return "dummy";
    }
}
