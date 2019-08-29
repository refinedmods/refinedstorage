package com.raoulvdberge.refinedstorage.tile.direction;

import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EnumFacing;

public class DirectionHandlerNetworkNode implements IDirectionHandler {
    private TileNode tile;

    public DirectionHandlerNetworkNode(TileNode tile) {
        this.tile = tile;
    }

    @Override
    public void setDirection(EnumFacing direction) {
        tile.getNode().setDirection(direction);
    }

    @Override
    public EnumFacing getDirection() {
        return tile.getNode().getDirection();
    }

    @Override
    public void writeToTileNbt(CompoundNBT tag) {
        // NO OP
    }

    @Override
    public void readFromTileNbt(CompoundNBT tag) {
        // NO OP
    }
}
