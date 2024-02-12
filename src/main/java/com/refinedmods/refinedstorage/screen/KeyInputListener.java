package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import com.refinedmods.refinedstorage.network.OpenNetworkItemMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
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
                    sendError(Component.translatable("misc.refinedstorage.network_item.shortcut_duplicate", Component.translatable(items[0].getDescriptionId())));
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
            sendError(Component.translatable("misc.refinedstorage.network_item.shortcut_not_found", Component.translatable(items[0].getDescriptionId())));
        } else {
            RS.NETWORK_HANDLER.sendToServer(new OpenNetworkItemMessage(new PlayerSlot(slotFound)));
        }
    }

    public static void sendError(MutableComponent error) {
        Minecraft.getInstance().player.sendSystemMessage(error);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key e) {
        if (Minecraft.getInstance().player != null) {
            if (RSKeyBindings.OPEN_WIRELESS_GRID.isDown()) {
                findAndOpen(RSItems.WIRELESS_GRID.get(), RSItems.CREATIVE_WIRELESS_GRID.get());
            } else if (RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.isDown()) {
                findAndOpen(RSItems.WIRELESS_FLUID_GRID.get(), RSItems.CREATIVE_WIRELESS_FLUID_GRID.get());
            } else if (RSKeyBindings.OPEN_PORTABLE_GRID.isDown()) {
                findAndOpen(RSItems.PORTABLE_GRID.get(), RSItems.CREATIVE_PORTABLE_GRID.get());
            } else if (RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.isDown()) {
                findAndOpen(RSItems.WIRELESS_CRAFTING_MONITOR.get(), RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR.get());
            }
        }
    }


}
