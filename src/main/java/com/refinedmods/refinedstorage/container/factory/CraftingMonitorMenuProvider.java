package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CraftingMonitorContainerMenu;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.ICraftingMonitor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;

public class CraftingMonitorMenuProvider implements MenuProvider {
    private final ICraftingMonitor craftingMonitor;
    @Nullable
    private final CraftingMonitorBlockEntity blockEntity;
    private final MenuType<CraftingMonitorContainerMenu> containerType;

    public CraftingMonitorMenuProvider(MenuType<CraftingMonitorContainerMenu> containerType, ICraftingMonitor craftingMonitor, @Nullable CraftingMonitorBlockEntity blockEntity) {
        this.containerType = containerType;
        this.craftingMonitor = craftingMonitor;
        this.blockEntity = blockEntity;
    }

    @Override
    public Component getDisplayName() {
        return craftingMonitor.getTitle();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new CraftingMonitorContainerMenu(containerType, craftingMonitor, blockEntity, playerEntity, windowId);
    }
}
