package com.raoulvdberge.refinedstorage.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeyInputListener {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        PlayerInventory inv = Minecraft.getInstance().player.inventory;

        /*TODO if (RSKeyBindings.OPEN_WIRELESS_GRID.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_GRID);
        } else if (RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_FLUID_GRID);
        } else if (RSKeyBindings.OPEN_PORTABLE_GRID.isKeyDown()) {
            findAndOpen(inv, Item.getItemFromBlock(RSBlocks.PORTABLE_GRID));
        } else if (RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_CRAFTING_MONITOR);
        }*/
    }

    private void findAndOpen(IInventory inv, Item search) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (slot.getItem() == search) {
                // TODO RS.INSTANCE.network.sendToServer(new MessageNetworkItemOpen(i));

                return;
            }
        }
    }
}
