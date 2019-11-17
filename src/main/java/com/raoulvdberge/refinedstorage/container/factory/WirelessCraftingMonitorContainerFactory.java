package com.raoulvdberge.refinedstorage.container.factory;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.CraftingMonitorContainer;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;

public class WirelessCraftingMonitorContainerFactory implements IContainerFactory<CraftingMonitorContainer> {
    @Override
    public CraftingMonitorContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
        int slotId = data.readInt();

        ItemStack stack = inv.getStackInSlot(slotId);

        WirelessCraftingMonitor wirelessCraftingMonitor = new WirelessCraftingMonitor(stack, null, slotId);

        return new CraftingMonitorContainer(RSContainers.WIRELESS_CRAFTING_MONITOR, wirelessCraftingMonitor, null, inv.player, windowId);
    }
}
