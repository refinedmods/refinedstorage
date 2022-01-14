package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.network.OpenNetworkItemMessage;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class KeyInputListener {
    //These are static to be accessible from RSAddons
    public static void findAndOpen(Item... items) {
        Set<Item> validItems = new HashSet<>(Arrays.asList(items));
        Container inv = Minecraft.getInstance().player.getInventory();
        int slotFound = -1;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack slot = inv.getItem(i);

            if (validItems.contains(slot.getItem())) {
                if (slotFound != -1) {
                    sendError(new TranslatableComponent("misc.refinedstorage.network_item.shortcut_duplicate", new TranslatableComponent(items[0].getDescriptionId())));
                    return;
                }

                slotFound = i;
            }
        }

        if (CuriosIntegration.isLoaded() && slotFound == -1) {
            Optional<ImmutableTriple<String, Integer, ItemStack>> curio = CuriosApi.getCuriosHelper().findEquippedCurio(stack -> validItems.contains(stack.getItem()), Minecraft.getInstance().player);

            if (curio.isPresent()) {
                RS.NETWORK_HANDLER.sendToServer(new OpenNetworkItemMessage(new PlayerSlot(curio.get().getMiddle(), curio.get().getLeft())));
                return;
            }
        }

        if (slotFound == -1) {
            sendError(new TranslatableComponent("misc.refinedstorage.network_item.shortcut_not_found", new TranslatableComponent(items[0].getDescriptionId())));
        } else {
            RS.NETWORK_HANDLER.sendToServer(new OpenNetworkItemMessage(new PlayerSlot(slotFound)));
        }
    }

    public static void sendError(TranslatableComponent error) {
        Minecraft.getInstance().player.sendMessage(error, Util.NIL_UUID);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (e.getAction() != 1 || Minecraft.getInstance().player == null) return;

        if (e.getKey() == RSKeyBindings.OPEN_WIRELESS_GRID.getKey())
        {
            findAndOpen(RSItems.WIRELESS_GRID.get(), RSItems.CREATIVE_WIRELESS_GRID.get());
        } 
        else if (e.getKey() == RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.getKey())
        {
            findAndOpen(RSItems.WIRELESS_FLUID_GRID.get(), RSItems.CREATIVE_WIRELESS_FLUID_GRID.get());
        } 
        else if (e.getKey() == RSKeyBindings.OPEN_PORTABLE_GRID.getKey())
        {
            findAndOpen(RSItems.PORTABLE_GRID.get(), RSItems.CREATIVE_PORTABLE_GRID.get());
        }
        else if (e.getKey() == RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.getKey())
        {
            findAndOpen(RSItems.WIRELESS_CRAFTING_MONITOR.get(), RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR.get());
        }
    }


}
