package com.raoulvdberge.refinedstorage.tile;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface INetworkNodeHolder {
    EnumFacing getDirection();

    void setDirection(EnumFacing direction);

    World world();

    BlockPos pos();
}
