package com.raoulvdberge.refinedstorage.api.autocrafting;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Event fired upon completion of an auto crafting task
 */
public class AutoCraftingEvent extends Event {
    private ItemStack crafted;
    private INetworkMaster network;

    private AutoCraftingEvent(INetworkMaster network, ItemStack crafted) {
        this.crafted = crafted;
        this.network = network;
    }

    public ItemStack getCrafted() {
        return crafted;
    }

    public static void fire(INetworkMaster network, ItemStack crafted) {
        MinecraftForge.EVENT_BUS.post(new AutoCraftingEvent(network, crafted));
    }

    public static void fire(INetworkMaster network, ItemStack crafted, int quantity) {
        fire(network, ItemHandlerHelper.copyStackWithSize(crafted, quantity));
    }
}
