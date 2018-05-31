package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class ItemWirelessCraftingMonitor extends ItemNetworkItem {
    public static final String NBT_SIZE = "Size";

    public ItemWirelessCraftingMonitor() {
        super("wireless_crafting_monitor");
    }

    @Override
    @Nonnull
    public INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
        return new NetworkItemWirelessCraftingMonitor(handler, player, stack);
    }

    public static int getSize(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_SIZE)) {
            return stack.getTagCompound().getInteger(NBT_SIZE);
        }

        return IGrid.SIZE_STRETCH;
    }

    public static void setSize(ItemStack stack, int size) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setInteger(NBT_SIZE, size);
    }
}
