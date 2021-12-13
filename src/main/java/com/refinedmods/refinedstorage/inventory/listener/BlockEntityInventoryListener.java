package com.refinedmods.refinedstorage.inventory.listener;

import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityInventoryListener implements InventoryListener<BaseItemHandler> {
    private final BlockEntity blockEntity;

    public BlockEntityInventoryListener(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public void onChanged(BaseItemHandler handler, int slot, boolean reading) {
        if (!reading) {
            blockEntity.setChanged();
        }
    }
}
