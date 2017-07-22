package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessCraftingMonitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ItemWirelessCraftingMonitor extends ItemNetworkItem {
    private static final String NBT_VIEW_AUTOMATED = "ViewAutomated";

    public ItemWirelessCraftingMonitor() {
        super("wireless_crafting_monitor");
    }

    @Override
    @Nonnull
    public INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
        return new NetworkItemWirelessCraftingMonitor(handler, player, stack);
    }

    public static void setViewAutomated(ItemStack stack, boolean viewAutomated) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setBoolean(NBT_VIEW_AUTOMATED, viewAutomated);
    }

    public static boolean canViewAutomated(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_VIEW_AUTOMATED)) ? stack.getTagCompound().getBoolean(NBT_VIEW_AUTOMATED) : true;
    }
}
