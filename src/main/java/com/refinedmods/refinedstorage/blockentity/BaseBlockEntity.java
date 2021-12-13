package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseBlockEntity extends BlockEntity {
    protected final BlockEntitySynchronizationManager dataManager = new BlockEntitySynchronizationManager(this);

    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockEntitySynchronizationManager getDataManager() {
        return dataManager;
    }

    public CompoundTag writeUpdate(CompoundTag tag) {
        return tag;
    }

    public void readUpdate(CompoundTag tag) {
    }

    @Override
    public final CompoundTag getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        readUpdate(packet.getTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        readUpdate(tag);
    }

    // @Volatile: Copied with some changes from the super method (avoid sending neighbor updates, it's not needed)
    @Override
    public void setChanged() {
        if (level != null) {
            level.blockEntityChanged(worldPosition);
        }
    }
}
