package com.raoulvdberge.refinedstorage.api.network.node;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface INetworkNodeFactory {
    @Nonnull
    INetworkNode create(NBTTagCompound tag, World world, BlockPos pos);
}
