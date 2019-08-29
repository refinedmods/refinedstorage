package com.raoulvdberge.refinedstorage.tile.direction;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EnumFacing;

public interface IDirectionHandler {
    void setDirection(EnumFacing direction);

    EnumFacing getDirection();

    void writeToTileNbt(CompoundNBT tag);

    void readFromTileNbt(CompoundNBT tag);
}
