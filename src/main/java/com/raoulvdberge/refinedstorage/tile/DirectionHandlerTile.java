package com.raoulvdberge.refinedstorage.tile;

import net.minecraft.nbt.NBTTagCompound;
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
    public void writeToTileNbt(NBTTagCompound tag) {
        tag.setInteger(NBT_DIRECTION, direction.ordinal());
    }

    @Override
    public void readFromTileNbt(NBTTagCompound tag) {
        if (tag.hasKey(NBT_DIRECTION)) {
            direction = EnumFacing.byIndex(tag.getInteger(NBT_DIRECTION));
        }
    }
}
