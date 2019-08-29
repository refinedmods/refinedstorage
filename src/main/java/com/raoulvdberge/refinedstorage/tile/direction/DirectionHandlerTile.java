package com.raoulvdberge.refinedstorage.tile.direction;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EnumFacing;

public class DirectionHandlerTile implements IDirectionHandler {
    public static final String NBT_DIRECTION = "Direction";

    private EnumFacing direction = EnumFacing.NORTH;

    @Override
    public void setDirection(EnumFacing direction) {
        this.direction = direction;
    }

    @Override
    public EnumFacing getDirection() {
        return direction;
    }

    @Override
    public void writeToTileNbt(CompoundNBT tag) {
        tag.putInt(NBT_DIRECTION, direction.ordinal());
    }

    @Override
    public void readFromTileNbt(CompoundNBT tag) {
        if (tag.hasKey(NBT_DIRECTION)) {
            direction = EnumFacing.byIndex(tag.getInteger(NBT_DIRECTION));
        }
    }
}
