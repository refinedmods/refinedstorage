package com.raoulvdberge.refinedstorage.item.filter;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFilterItems;
import com.raoulvdberge.refinedstorage.item.ItemBase;
import com.raoulvdberge.refinedstorage.item.ItemPattern;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFilter extends ItemBase {
    public static final int MODE_WHITELIST = 0;
    public static final int MODE_BLACKLIST = 1;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_MOD_FILTER = "ModFilter";
    private static final String NBT_NAME = "Name";
    private static final String NBT_ICON = "Icon";

    private static Map<ItemStack, NonNullList<ItemStack>> FILTER_CACHE = new HashMap<>();

    public ItemFilter() {
        super("filter");

        setMaxStackSize(1);
    }

    public static NonNullList<ItemStack> getFilterItemsFromCache(ItemStack stack) {
        if (!FILTER_CACHE.containsKey(stack)) {
            ItemHandlerFilterItems items = new ItemHandlerFilterItems(stack);

            FILTER_CACHE.put(stack, items.getFilteredItems());
        }

        return FILTER_CACHE.get(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            if (player.isSneaking()) {
                return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSItems.FILTER));
            }

            player.openGui(RS.INSTANCE, RSGui.FILTER, world, hand.ordinal(), 0, 0);

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = getName(stack);

        if (!name.equalsIgnoreCase("")) {
            return name;
        }

        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(TextFormatting.YELLOW + I18n.format("sidebutton.refinedstorage:mode." + (getMode(stack) == MODE_WHITELIST ? "whitelist" : "blacklist")) + TextFormatting.RESET);

        if (isModFilter(stack)) {
            tooltip.add(TextFormatting.BLUE + I18n.format("gui.refinedstorage:filter.mod_filter") + TextFormatting.RESET);
        }

        ItemHandlerFilterItems items = new ItemHandlerFilterItems(stack);

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

    public static int getMode(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_MODE)) ? stack.getTagCompound().getInteger(NBT_MODE) : MODE_WHITELIST;
    }

    public static void setMode(ItemStack stack, int mode) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setInteger(NBT_MODE, mode);
    }

    public static boolean isModFilter(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_MOD_FILTER) && stack.getTagCompound().getBoolean(NBT_MOD_FILTER);
    }

    public static void setModFilter(ItemStack stack, boolean modFilter) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setBoolean(NBT_MOD_FILTER, modFilter);
    }

    public static String getName(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_NAME) ? stack.getTagCompound().getString(NBT_NAME) : "";
    }

    public static void setName(ItemStack stack, String name) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setString(NBT_NAME, name);
    }

    public static ItemStack getIcon(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ICON) ? new ItemStack(stack.getTagCompound().getCompoundTag(NBT_ICON)) : ItemStack.EMPTY;
    }

    public static void setIcon(ItemStack stack, ItemStack icon) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setTag(NBT_ICON, icon.serializeNBT());
    }
}
