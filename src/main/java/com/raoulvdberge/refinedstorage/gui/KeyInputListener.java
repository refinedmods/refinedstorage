package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.network.MessageNetworkItemOpen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyInputListener {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;

        if (RSKeyBindings.OPEN_WIRELESS_GRID.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_GRID);
        } else if (RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_FLUID_GRID);
        } else if (RSKeyBindings.OPEN_PORTABLE_GRID.isKeyDown()) {
            findAndOpen(inv, Item.getItemFromBlock(RSBlocks.PORTABLE_GRID));
        } else if (RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_CRAFTING_MONITOR);
        }
    }

    private void findAndOpen(IInventory inv, Item search) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (slot.getItem() == search) {
                RS.INSTANCE.network.sendToServer(new MessageNetworkItemOpen(i));

                return;
            }
        }
    }
}
