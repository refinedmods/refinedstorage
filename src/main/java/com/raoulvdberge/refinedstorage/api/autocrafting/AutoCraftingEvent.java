package com.raoulvdberge.refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Event fired upon completion of an auto crafting task
 */
public class AutoCraftingEvent extends Event {
    private ItemStack crafted;

    private AutoCraftingEvent(ItemStack crafted) {
        this.crafted = crafted;
    }

    public ItemStack getCrafted() {
        return crafted;
    }

    public static void fire(ItemStack crafted) {
        MinecraftForge.EVENT_BUS.post(new AutoCraftingEvent(crafted));
    }

    public static void fire(ItemStack crafted, int quantity) {
        fire(ItemHandlerHelper.copyStackWithSize(crafted, quantity));
    }
}
