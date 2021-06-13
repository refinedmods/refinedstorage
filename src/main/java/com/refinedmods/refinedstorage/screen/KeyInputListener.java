package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.RSKeyBindings;
import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.network.OpenNetworkItemMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class KeyInputListener {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (Minecraft.getInstance().player != null) {
            if (RSKeyBindings.OPEN_WIRELESS_GRID.isKeyDown()) {
                findAndOpen(RSItems.WIRELESS_GRID.get(), RSItems.CREATIVE_WIRELESS_GRID.get());
            } else if (RSKeyBindings.OPEN_WIRELESS_FLUID_GRID.isKeyDown()) {
                findAndOpen(RSItems.WIRELESS_FLUID_GRID.get(), RSItems.CREATIVE_WIRELESS_FLUID_GRID.get());
            } else if (RSKeyBindings.OPEN_PORTABLE_GRID.isKeyDown()) {
                findAndOpen(RSItems.PORTABLE_GRID.get(), RSItems.CREATIVE_PORTABLE_GRID.get());
            } else if (RSKeyBindings.OPEN_WIRELESS_CRAFTING_MONITOR.isKeyDown()) {
                findAndOpen(RSItems.WIRELESS_CRAFTING_MONITOR.get(), RSItems.CREATIVE_WIRELESS_CRAFTING_MONITOR.get());
            }
        }
    }

    public void findAndOpen(Item... items) {
        Set<Item> validItems = new HashSet<>(Arrays.asList(items));
        IInventory inv = Minecraft.getInstance().player.inventory;
        int slotFound = -1;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (validItems.contains(slot.getItem())) {
                if (slotFound != -1) {
                    sendError(new TranslationTextComponent("misc.refinedstorage.network_item.shortcut_duplicate", new TranslationTextComponent(items[0].getTranslationKey())));
                    return;
                }

                slotFound = i;
            }
        }
        if (CuriosIntegration.isLoaded() && slotFound == -1) {
            AtomicReference<Boolean> found = new AtomicReference<>();
            LazyOptional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(Minecraft.getInstance().player);

            if (curiosHandler.isPresent()) {
                curiosHandler.resolve().get().getCurios().forEach((name, handler) -> {
                    for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                        if (validItems.contains(handler.getStacks().getStackInSlot(i).getItem())) {
                            found.set(true);
                            RS.NETWORK_HANDLER.sendToServer(new OpenNetworkItemMessage(i, name));
                            return;
                        }
                    }
                });
            }

            if (found.get()) {
                return;
            }
        }


        if (slotFound == -1) {
            sendError(new TranslationTextComponent("misc.refinedstorage.network_item.shortcut_not_found", new TranslationTextComponent(items[0].getTranslationKey())));
        } else {
            RS.NETWORK_HANDLER.sendToServer(new OpenNetworkItemMessage(slotFound, ""));
        }
    }

    public void sendError(TranslationTextComponent error) {
        Minecraft.getInstance().player.sendMessage(error, Util.DUMMY_UUID);
    }


}
