package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBlockPortableGrid extends ItemBlockEnergyItem {
    public ItemBlockPortableGrid() {
        super(RSBlocks.PORTABLE_GRID, RSBlocks.PORTABLE_GRID.getDirection());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            player.openGui(RS.INSTANCE, RSGui.WIRELESS_GRID, player.getEntityWorld(), hand.ordinal(), 0, PortableGrid.GRID_TYPE);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack.getItem() == newStack.getItem()) {
            if (ItemWirelessGrid.getSortingDirection(oldStack) == ItemWirelessGrid.getSortingDirection(newStack) &&
                ItemWirelessGrid.getSortingType(oldStack) == ItemWirelessGrid.getSortingType(newStack) &&
                ItemWirelessGrid.getSearchBoxMode(oldStack) == ItemWirelessGrid.getSearchBoxMode(newStack) &&
                ItemWirelessGrid.getTabSelected(oldStack) == ItemWirelessGrid.getTabSelected(newStack) &&
                ItemWirelessGrid.getSize(oldStack) == ItemWirelessGrid.getSize(newStack)) {
                return false;
            }
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }
}
