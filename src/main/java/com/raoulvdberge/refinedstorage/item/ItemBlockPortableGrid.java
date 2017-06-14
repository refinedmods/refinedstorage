package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(I18n.format("block.refinedstorage:portable_grid.tooltip"));
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            return EnumActionResult.FAIL;
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
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
