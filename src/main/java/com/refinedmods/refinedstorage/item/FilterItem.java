package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.FilterContainer;
import com.refinedmods.refinedstorage.inventory.fluid.ConfiguredFluidsInFilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.ConfiguredItemsInFilterItemHandler;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.util.RenderUtils;
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
        super(new Item.Properties().tab(RS.MAIN_GROUP).stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide) {
            if (player.isCrouching()) {
                return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSItems.FILTER.get()));
            }

            player.openMenu(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("gui.refinedstorage.filter");
                }

                @Override
                public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
                    return new FilterContainer(player, inventory.getSelected(), windowId);
                }
            });
        }

        return new ActionResult<>(ActionResultType.CONSUME, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(new TranslationTextComponent("sidebutton.refinedstorage.mode." + (getMode(stack) == IFilter.MODE_WHITELIST ? "whitelist" : "blacklist")).setStyle(Styles.YELLOW));

        if (isModFilter(stack)) {
            tooltip.add(new TranslationTextComponent("gui.refinedstorage.filter.mod_filter").setStyle(Styles.BLUE));
        }

        RenderUtils.addCombinedItemsToTooltip(tooltip, false, new ConfiguredItemsInFilterItemHandler(stack).getConfiguredItems());
        RenderUtils.addCombinedFluidsToTooltip(tooltip, false, new ConfiguredFluidsInFilterItemHandler(stack).getConfiguredFluids());
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

    public static String getFilterName(ItemStack stack) {
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
        return stack.hasTag() && stack.getTag().contains(NBT_ICON) ? ItemStack.of(stack.getTag().getCompound(NBT_ICON)) : ItemStack.EMPTY;
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

    @Nonnull
    public static FluidStack getFluidIcon(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_FLUID_ICON) ? FluidStack.loadFluidStackFromNBT(stack.getTag().getCompound(NBT_FLUID_ICON)) : FluidStack.EMPTY;
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
