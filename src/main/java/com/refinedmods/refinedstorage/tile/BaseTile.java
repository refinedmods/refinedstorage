package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public abstract class BaseTile extends TileEntity {
    protected TileDataManager dataManager = new TileDataManager(this);

    public BaseTile(TileEntityType<?> tileType) {
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

    // @Volatile: Copied with some changes from the super method (avoid sending neighbor updates, it's not needed)
    @Override
    public void markDirty() {
        if (world != null) {
            world.markChunkDirty(pos, this);
        }
    }
}
