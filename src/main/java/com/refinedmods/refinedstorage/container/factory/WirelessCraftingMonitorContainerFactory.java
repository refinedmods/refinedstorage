package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainerMenu;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.IContainerFactory;

public class WirelessCraftingMonitorContainerFactory implements IContainerFactory<CraftingMonitorContainerMenu> {
    @Override
    public CraftingMonitorContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {

        PlayerSlot slot = new PlayerSlot(data);

        ItemStack stack = slot.getStackFromSlot(inv.player);

        WirelessCraftingMonitor wirelessCraftingMonitor = new WirelessCraftingMonitor(stack, null, slot);

        return new CraftingMonitorContainerMenu(RSContainerMenus.WIRELESS_CRAFTING_MONITOR.get(), wirelessCraftingMonitor, null, inv.player, windowId);
    }
}
