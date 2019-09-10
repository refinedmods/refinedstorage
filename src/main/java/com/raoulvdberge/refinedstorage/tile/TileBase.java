package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.direction.DirectionHandlerTile;
import com.raoulvdberge.refinedstorage.tile.direction.IDirectionHandler;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public abstract class TileBase extends TileEntity {
    protected static final String NBT_DIRECTION = "Direction";

    private Direction clientDirection = Direction.NORTH;
    protected IDirectionHandler directionHandler = new DirectionHandlerTile();
    protected TileDataManager dataManager = new TileDataManager(this);

    public void setDirection(Direction direction) {
        clientDirection = direction;

        directionHandler.setDirection(direction);

        world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());

        markDirty();
    }

    public Direction getDirection() {
        return world.isRemote ? clientDirection : directionHandler.getDirection();
    }

    public TileDataManager getDataManager() {
        return dataManager;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);

        directionHandler.writeToTileNbt(tag);

        return tag;
    }

    public CompoundNBT writeUpdate(CompoundNBT tag) {
        tag.putInt(NBT_DIRECTION, directionHandler.getDirection().ordinal());

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        directionHandler.readFromTileNbt(tag);
    }

    public void readUpdate(CompoundNBT tag) {
        boolean doRender = canCauseRenderUpdate(tag);

        clientDirection = Direction.byIndex(tag.getInt(NBT_DIRECTION));

        if (doRender) {
            WorldUtils.updateBlock(world, pos);
        }
    }

    protected boolean canCauseRenderUpdate(CompoundNBT tag) {
        return true;
    }

    @Override
    public final CompoundNBT getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Nullable
    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        readUpdate(packet.getNbtCompound());
    }

    @Override
    public final void handleUpdateTag(CompoundNBT tag) {
        super.read(tag);

        readUpdate(tag);
    }

    @Nullable
    public IItemHandler getDrops() {
        return null;
    }

    // @Volatile: Copied with some changes from the super method (avoid sending neighbor updates, it's not needed)
    @Override
    public void markDirty() {
        if (world != null) {
            world.markChunkDirty(pos, this);
        }
    }
}
