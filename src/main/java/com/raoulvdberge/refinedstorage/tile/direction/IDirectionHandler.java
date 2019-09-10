package com.raoulvdberge.refinedstorage.tile.direction;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public interface IDirectionHandler {
    void setDirection(Direction direction);

    Direction getDirection();

    void writeToTileNbt(CompoundNBT tag);

    void readFromTileNbt(CompoundNBT tag);
}
