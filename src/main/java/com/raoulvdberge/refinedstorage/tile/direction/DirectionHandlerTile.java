package com.raoulvdberge.refinedstorage.tile.direction;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class DirectionHandlerTile implements IDirectionHandler {
    public static final String NBT_DIRECTION = "Direction";

    private Direction direction = Direction.NORTH;

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void writeToTileNbt(CompoundNBT tag) {
        tag.putInt(NBT_DIRECTION, direction.ordinal());
    }

    @Override
    public void readFromTileNbt(CompoundNBT tag) {
        if (tag.hasKey(NBT_DIRECTION)) {
            direction = Direction.byIndex(tag.getInteger(NBT_DIRECTION));
        }
    }
}
