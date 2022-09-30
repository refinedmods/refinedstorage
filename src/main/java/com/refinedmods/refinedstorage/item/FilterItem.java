package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.container.FilterContainerMenu;
import com.refinedmods.refinedstorage.inventory.fluid.ConfiguredFluidsInFilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.ConfiguredItemsInFilterItemHandler;
import com.refinedmods.refinedstorage.render.Styles;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FilterItem extends Item {
    public static final String NBT_FLUID_FILTERS = "FluidFilters";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_MOD_FILTER = "ModFilter";
    private static final String NBT_NAME = "Name";
    private static final String NBT_ICON = "Icon";
    private static final String NBT_FLUID_ICON = "FluidIcon";
    private static final String NBT_TYPE = "Type";

    public FilterItem() {
        super(new Item.Properties().tab(RS.CREATIVE_MODE_TAB).stacksTo(1));
    }

    public static int getCompare(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_COMPARE)) ? stack.getTag().getInt(NBT_COMPARE) : IComparer.COMPARE_NBT;
    }

    public static void setCompare(ItemStack stack, int compare) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putInt(NBT_COMPARE, compare);
    }

    public static int getMode(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(NBT_MODE)) ? stack.getTag().getInt(NBT_MODE) : IFilter.MODE_WHITELIST;
    }

    public static void setMode(ItemStack stack, int mode) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putInt(NBT_MODE, mode);
    }

    public static boolean isModFilter(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_MOD_FILTER) && stack.getTag().getBoolean(NBT_MOD_FILTER);
    }

    public static void setModFilter(ItemStack stack, boolean modFilter) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putBoolean(NBT_MOD_FILTER, modFilter);
    }

    public static String getFilterName(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_NAME) ? stack.getTag().getString(NBT_NAME) : "";
    }

    public static void setName(ItemStack stack, String name) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putString(NBT_NAME, name);
    }

    @Nonnull
    public static ItemStack getIcon(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(NBT_ICON) ? ItemStack.of(stack.getTag().getCompound(NBT_ICON)) : ItemStack.EMPTY;
    }

    public static void setIcon(ItemStack stack, ItemStack icon) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().put(NBT_ICON, icon.serializeNBT());
    }

    public static void setFluidIcon(ItemStack stack, @Nullable FluidStack icon) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }

        if (icon == null) {
            stack.getTag().remove(NBT_FLUID_ICON);
        } else {
            stack.getTag().put(NBT_FLUID_ICON, icon.writeToNBT(new CompoundTag()));
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
            stack.setTag(new CompoundTag());
        }

        stack.getTag().putInt(NBT_TYPE, type);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            if (player.isCrouching()) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, new ItemStack(RSItems.FILTER.get()));
            }

            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("gui.refinedstorage.filter");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return new FilterContainerMenu(player, inventory.getSelected(), windowId);
                }
            });
        }

        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.translatable("sidebutton.refinedstorage.mode." + (getMode(stack) == IFilter.MODE_WHITELIST ? "whitelist" : "blacklist")).setStyle(Styles.YELLOW));

        if (isModFilter(stack)) {
            tooltip.add(Component.translatable("gui.refinedstorage.filter.mod_filter").setStyle(Styles.BLUE));
        }

        RenderUtils.addCombinedItemsToTooltip(tooltip, false, new ConfiguredItemsInFilterItemHandler(stack).getConfiguredItems());
        RenderUtils.addCombinedFluidsToTooltip(tooltip, false, new ConfiguredFluidsInFilterItemHandler(stack).getConfiguredFluids());
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
