package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile;
import com.refinedmods.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public class CraftingMonitorContainerProvider implements MenuProvider {
    private final ICraftingMonitor craftingMonitor;
    @Nullable
    private final CraftingMonitorTile tile;
    private final MenuType<CraftingMonitorContainer> containerType;

    public CraftingMonitorContainerProvider(MenuType<CraftingMonitorContainer> containerType, ICraftingMonitor craftingMonitor, @Nullable CraftingMonitorTile tile) {
        this.containerType = containerType;
        this.craftingMonitor = craftingMonitor;
        this.tile = tile;
    }

    @Override
    public Component getDisplayName() {
        return craftingMonitor.getTitle();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new CraftingMonitorContainer(containerType, craftingMonitor, tile, playerEntity, windowId);
    }
}
