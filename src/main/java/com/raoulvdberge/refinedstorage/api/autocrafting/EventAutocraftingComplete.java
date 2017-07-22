package com.raoulvdberge.refinedstorage.api.autocrafting;

import com.raoulvdberge.refinedstorage.api.network.INetwork;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Event fired upon completion of an auto crafting task.
 */
public class EventAutocraftingComplete extends Event {
    private ItemStack crafted;
    private INetwork network;

    private EventAutocraftingComplete(INetwork network, ItemStack crafted) {
        this.crafted = crafted;
        this.network = network;
    }

    public ItemStack getCrafted() {
        return crafted;
    }

    public INetwork getNetwork() {
        return network;
    }

    public static void fire(INetwork network, ItemStack crafted) {
        MinecraftForge.EVENT_BUS.post(new EventAutocraftingComplete(network, crafted));
    }

    public static void fire(INetwork network, ItemStack crafted, int quantity) {
        fire(network, ItemHandlerHelper.copyStackWithSize(crafted, quantity));
    }
}