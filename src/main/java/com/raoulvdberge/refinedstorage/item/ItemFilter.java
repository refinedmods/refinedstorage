package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterItems;
import com.raoulvdberge.refinedstorage.item.info.ItemInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFilter extends ItemBase {
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_MOD_FILTER = "ModFilter";
    private static final String NBT_NAME = "Name";
    private static final String NBT_ICON = "Icon";
    private static final String NBT_FLUID_ICON = "FluidIcon";
    private static final String NBT_TYPE = "Type";
    public static final String NBT_FLUID_FILTERS = "FluidFilters";

    public ItemFilter() {
        super(new ItemInfo(RS.ID, "filter"));

        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(TextFormatting.YELLOW + I18n.format("sidebutton.refinedstorage:mode." + (getMode(stack) == IFilter.MODE_WHITELIST ? "whitelist" : "blacklist")) + TextFormatting.RESET);

        if (isModFilter(stack)) {
            tooltip.add(TextFormatting.BLUE + I18n.format("gui.refinedstorage:filter.mod_filter") + TextFormatting.RESET);
        }

        ItemHandlerFilterItems items = new ItemHandlerFilterItems(stack);

        RenderUtils.addCombinedItemsToTooltip(tooltip, false, items.getFilteredItems());

        FluidInventoryFilter fluids = new FluidInventoryFilter(stack);

        RenderUtils.addCombinedFluidsToTooltip(tooltip, false, fluids.getFilteredFluids());
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
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_MODE)) ? stack.getTagCompound().getInteger(NBT_MODE) : IFilter.MODE_WHITELIST;
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

    @Nonnull
    public static ItemStack getIcon(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ICON) ? new ItemStack(stack.getTagCompound().getCompoundTag(NBT_ICON)) : ItemStack.EMPTY;
    }

    public static void setIcon(ItemStack stack, ItemStack icon) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setTag(NBT_ICON, icon.serializeNBT());
    }

    public static void setFluidIcon(ItemStack stack, @Nullable FluidStack icon) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (icon == null) {
            stack.getTagCompound().removeTag(NBT_FLUID_ICON);
        } else {
            stack.getTagCompound().setTag(NBT_FLUID_ICON, icon.writeToNBT(new NBTTagCompound()));
        }
    }

    @Nullable
    public static FluidStack getFluidIcon(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_FLUID_ICON) ? FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(NBT_FLUID_ICON)) : null;
    }

    public static int getType(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_TYPE) ? stack.getTagCompound().getInteger(NBT_TYPE) : IType.ITEMS;
    }

    public static void setType(ItemStack stack, int type) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setInteger(NBT_TYPE, type);
    }
}
