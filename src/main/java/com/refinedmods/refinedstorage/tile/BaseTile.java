package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class BaseTile extends TileEntity {
    protected final TileDataManager dataManager = new TileDataManager(this);

    protected BaseTile(TileEntityType<?> tileType) {
        super(tileType);
    }

    public TileDataManager getDataManager() {
        return dataManager;
    }

    public CompoundNBT writeUpdate(CompoundNBT tag) {
        return tag;
    }

    public void readUpdate(CompoundNBT tag) {
    }

    @Override
    public final CompoundNBT getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        readUpdate(packet.getTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        readUpdate(tag);
    }

    // @Volatile: Copied with some changes from the super method (avoid sending neighbor updates, it's not needed)
    @Override
    public void setChanged() {
        if (level != null) {
            level.blockEntityChanged(worldPosition, this);
        }
    }
}
