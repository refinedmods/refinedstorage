package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerGridFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class ItemGridFilter extends ItemBase {
    private static final String NBT_COMPARE = "Compare";

    public ItemGridFilter() {
        super("grid_filter");

        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSItems.GRID_FILTER));
            }

            player.openGui(RS.INSTANCE, RSGui.GRID_FILTER, world, hand.ordinal(), 0, 0);

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        ItemHandlerGridFilter items = new ItemHandlerGridFilter(stack);

        ItemPattern.combineItems(tooltip, false, items.getFilteredItems());
    }

    public static int getCompare(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_COMPARE)) ? stack.getTagCompound().getInteger(NBT_COMPARE) : (IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    public static void setCompare(ItemStack stack, int compare) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setInteger(NBT_COMPARE, compare);
    }
}
