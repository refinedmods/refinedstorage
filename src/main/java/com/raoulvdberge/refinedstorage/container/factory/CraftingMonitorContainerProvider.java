package com.raoulvdberge.refinedstorage.container.factory;

import com.raoulvdberge.refinedstorage.container.CraftingMonitorContainer;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class CraftingMonitorContainerProvider implements INamedContainerProvider {
    private ICraftingMonitor craftingMonitor;
    @Nullable
    private CraftingMonitorTile tile;

    public CraftingMonitorContainerProvider(ICraftingMonitor craftingMonitor, @Nullable CraftingMonitorTile tile) {
        this.craftingMonitor = craftingMonitor;
        this.tile = tile;
    }

    @Override
    public ITextComponent getDisplayName() {
        return craftingMonitor.getTitle();
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CraftingMonitorContainer(craftingMonitor, tile, playerEntity, windowId);
    }
}
