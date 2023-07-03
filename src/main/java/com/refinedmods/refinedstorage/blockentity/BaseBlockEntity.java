package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseBlockEntity extends BlockEntity {
    private final BlockEntitySynchronizationManager dataManager;
    private boolean unloaded;

    protected BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockEntitySynchronizationSpec syncSpec) {
        super(type, pos, state);
        this.dataManager = new BlockEntitySynchronizationManager(this, syncSpec);
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

    @Override
    public void setRemoved() {
        super.setRemoved();
        // @Volatile: MC calls setRemoved when a chunk unloads now as well (see ServerLevel#unload -> LevelChunk#clearAllBlockEntities).
        // Since we don't want to remove network node data in that case, we need to know if it was removed due to unloading.
        // We can use "unloaded" for that, it's set in #onChunkUnloaded.
        // Since MC first calls #onChunkUnloaded and then #setRemoved, this check keeps working.
        if (!unloaded) {
            onRemovedNotDueToChunkUnload();
        }
    }

    protected void onRemovedNotDueToChunkUnload() {
        // NO OP
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloaded = true;
    }

    // @Volatile: Copied with some changes from the super method (avoid sending neighbor updates, it's not needed)
    @Override
    public void setChanged() {
        if (level != null) {
            level.blockEntityChanged(worldPosition);
        }
    }
}
