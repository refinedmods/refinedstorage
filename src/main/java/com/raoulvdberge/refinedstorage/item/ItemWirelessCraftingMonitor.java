package com.raoulvdberge.refinedstorage.item;

import com.google.common.base.Optional;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessCraftingMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ItemWirelessCraftingMonitor extends ItemNetworkItem {
    public static final String NBT_SIZE = "Size";
    public static final String NBT_TAB_SELECTED = "TabSelected";
    public static final String NBT_TAB_PAGE = "TabPage";

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

    public static Optional<UUID> getTabSelected(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(NBT_TAB_SELECTED)) {
            return Optional.of(stack.getTagCompound().getUniqueId(NBT_TAB_SELECTED));
        }

        return Optional.absent();
    }

    public static void setTabSelected(ItemStack stack, Optional<UUID> tabSelected) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (tabSelected.isPresent()) {
            stack.getTagCompound().setUniqueId(NBT_TAB_SELECTED, tabSelected.get());
        } else {
            stack.getTagCompound().removeTag(NBT_TAB_SELECTED + "Least");
            stack.getTagCompound().removeTag(NBT_TAB_SELECTED + "Most");
        }
    }

    public static int getTabPage(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_TAB_PAGE)) {
            return stack.getTagCompound().getInteger(NBT_TAB_PAGE);
        }

        return 0;
    }

    public static void setTabPage(ItemStack stack, int tabPage) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setInteger(NBT_TAB_PAGE, tabPage);
    }
}
