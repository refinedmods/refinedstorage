package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import com.raoulvdberge.refinedstorage.container.ContainerFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterItems;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FilterItem extends Item {
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_MOD_FILTER = "ModFilter";
    private static final String NBT_NAME = "Name";
    private static final String NBT_ICON = "Icon";
    private static final String NBT_FLUID_ICON = "FluidIcon";
    private static final String NBT_TYPE = "Type";
    public static final String NBT_FLUID_FILTERS = "FluidFilters";

    public FilterItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1));

        this.setRegistryName(RS.ID, "filter");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            if (player.isSneaking()) {
                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSItems.FILTER));
            }

            player.openContainer(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("gui.refinedstorage.filter");
                }

                @Nullable
                @Override
                public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
                    return new ContainerFilter(player, inventory.getCurrentItem(), windowId);
                }
            });

            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("sidebutton.refinedstorage.mode." + (getMode(stack) == IFilter.MODE_WHITELIST ? "whitelist" : "blacklist")).setStyle(new Style().setColor(TextFormatting.YELLOW)));

        if (isModFilter(stack)) {
            tooltip.add(new TranslationTextComponent("gui.refinedstorage.filter.mod_filter").setStyle(new Style().setColor(TextFormatting.BLUE)));
        }

        ItemHandlerFilterItems items = new ItemHandlerFilterItems(stack);

        RenderUtils.addCombinedItemsToTooltip(tooltip, false, items.getFilteredItems());

        FluidInventoryFilter fluids = new FluidInventoryFilter(stack);

        RenderUtils.addCombinedFluidsToTooltip(tooltip, false, fluids.getFilteredFluids());
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static int getCompare(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_COMPARE)) ? stack.getTag().getInt(NBT_COMPARE) : IComparer.COMPARE_NBT;
    }

    public static void setCompare(ItemStack stack, int compare) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putInt(NBT_COMPARE, compare);
    }

    public static int getMode(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_MODE)) ? stack.getTag().getInt(NBT_MODE) : IFilter.MODE_WHITELIST;
    }

    public static void setMode(ItemStack stack, int mode) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putInt(NBT_MODE, mode);
    }

    public static boolean isModFilter(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_MOD_FILTER) && stack.getTag().getBoolean(NBT_MOD_FILTER);
    }

    public static void setModFilter(ItemStack stack, boolean modFilter) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putBoolean(NBT_MOD_FILTER, modFilter);
    }

    public static String getName(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_NAME) ? stack.getTag().getString(NBT_NAME) : "";
    }

    public static void setName(ItemStack stack, String name) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putString(NBT_NAME, name);
    }

    @Nonnull
    public static ItemStack getIcon(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_ICON) ? ItemStack.read(stack.getTag().getCompound(NBT_ICON)) : ItemStack.EMPTY;
    }

    public static void setIcon(ItemStack stack, ItemStack icon) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().put(NBT_ICON, icon.serializeNBT());
    }

    public static void setFluidIcon(ItemStack stack, @Nullable FluidStack icon) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        if (icon == null) {
            stack.getTag().remove(NBT_FLUID_ICON);
        } else {
            stack.getTag().put(NBT_FLUID_ICON, icon.writeToNBT(new CompoundNBT()));
        }
    }

    @Nullable
    public static FluidStack getFluidIcon(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_FLUID_ICON) ? FluidStack.loadFluidStackFromNBT(stack.getTag().getCompound(NBT_FLUID_ICON)) : null;
    }

    public static int getType(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_TYPE) ? stack.getTag().getInt(NBT_TYPE) : IType.ITEMS;
    }

    public static void setType(ItemStack stack, int type) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundNBT());
        }

        stack.getTag().putInt(NBT_TYPE, type);
    }
}
