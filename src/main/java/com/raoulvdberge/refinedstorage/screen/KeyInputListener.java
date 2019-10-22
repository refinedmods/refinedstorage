package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSKeyBindings;
import com.raoulvdberge.refinedstorage.network.OpenNetworkItemMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class KeyInputListener {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        PlayerInventory inv = Minecraft.getInstance().player.inventory;

        if (RSKeyBindings.OPEN_WIRELESS_GRID.isKeyDown()) {
            findAndOpen(inv, (error) -> Minecraft.getInstance().player.sendMessage(error), RSItems.WIRELESS_GRID, RSItems.CREATIVE_WIRELESS_GRID);
        } else if (RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.isKeyDown()) {
            findAndOpen(inv, (error) -> Minecraft.getInstance().player.sendMessage(error), RSItems.WIRELESS_FLUID_GRID, RSItems.CREATIVE_WIRELESS_FLUID_GRID);
        }/* TODO else if (RSKeyBindings.OPEN_PORTABLE_GRID.isKeyDown()) {
            findAndOpen(inv, Item.getItemFromBlock(RSBlocks.PORTABLE_GRID));
        } else if (RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.isKeyDown()) {
            findAndOpen(inv, RSItems.WIRELESS_CRAFTING_MONITOR);
        }*/
    }

    private void findAndOpen(IInventory inv, Consumer<ITextComponent> onError, Item... items) {
        Set<Item> validItems = new HashSet<>(Arrays.asList(items));

        int slotFound = -1;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (validItems.contains(slot.getItem())) {
                if (slotFound != -1) {
                    onError.accept(new TranslationTextComponent("misc.refinedstorage.network_item.shortcut_duplicate", new TranslationTextComponent(items[0].getTranslationKey())));
                    return;
                }

                slotFound = i;
            }
        }

        if (slotFound == -1) {
            onError.accept(new TranslationTextComponent("misc.refinedstorage.network_item.shortcut_not_found", new TranslationTextComponent(items[0].getTranslationKey())));
        } else {
            RS.NETWORK_HANDLER.sendToServer(new OpenNetworkItemMessage(slotFound));
        }
    }
}
