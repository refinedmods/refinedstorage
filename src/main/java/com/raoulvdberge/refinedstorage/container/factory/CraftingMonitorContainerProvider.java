package com.raoulvdberge.refinedstorage.container.factory;

import com.raoulvdberge.refinedstorage.container.CraftingMonitorContainer;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class CraftingMonitorContainerProvider implements INamedContainerProvider {
    private final ICraftingMonitor craftingMonitor;
    @Nullable
    private final CraftingMonitorTile tile;
    private final ContainerType<CraftingMonitorContainer> containerType;

    public CraftingMonitorContainerProvider(ContainerType<CraftingMonitorContainer> containerType, ICraftingMonitor craftingMonitor, @Nullable CraftingMonitorTile tile) {
        this.containerType = containerType;
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
        return new CraftingMonitorContainer(containerType, craftingMonitor, tile, playerEntity, windowId);
    }
}
