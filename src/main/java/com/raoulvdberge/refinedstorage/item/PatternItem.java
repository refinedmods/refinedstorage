package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.render.tesr.PatternItemStackTileRenderer;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PatternItem extends Item implements ICraftingPatternProvider {
    private static Map<ItemStack, CraftingPattern> CACHE = new HashMap<>();

    private static final String NBT_VERSION = "Version";
    private static final String NBT_INPUT_SLOT = "Input_%d";
    private static final String NBT_OUTPUT_SLOT = "Output_%d";
    private static final String NBT_FLUID_INPUT_SLOT = "FluidInput_%d";
    private static final String NBT_FLUID_OUTPUT_SLOT = "FluidOutput_%d";
    private static final String NBT_EXACT = "Exact";
    private static final String NBT_PROCESSING = "Processing";

    private static final int VERSION = 1;

    public PatternItem() {
        super(new Item.Properties().group(RS.MAIN_GROUP).setTEISR(() -> PatternItemStackTileRenderer::new));

        this.setRegistryName(RS.ID, "pattern");
    }

    public static CraftingPattern fromCache(World world, ItemStack stack) {
        if (!CACHE.containsKey(stack)) {
            CACHE.put(stack, new CraftingPattern(world, null, stack));
        }

        return CACHE.get(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (!stack.hasTag()) {
            return;
        }

        ICraftingPattern pattern = fromCache(world, stack);

        Style yellow = new Style().setColor(TextFormatting.YELLOW);
        Style blue = new Style().setColor(TextFormatting.BLUE);
        Style red = new Style().setColor(TextFormatting.RED);

        if (pattern.isValid()) {
            if (ContainerScreen.hasShiftDown() || isProcessing(stack)) {
                tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.inputs").setStyle(yellow));

                RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getInputs().stream().map(i -> i.size() > 0 ? i.get(0) : ItemStack.EMPTY).collect(Collectors.toList()));
                RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidInputs());

                tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.outputs").setStyle(yellow));
            }

            RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getOutputs());
            RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidOutputs());

            if (isExact(stack)) {
                tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.exact").setStyle(blue));
            }

            if (isProcessing(stack)) {
                tooltip.add(new TranslationTextComponent("misc.refinedstorage.processing").setStyle(blue));
            }
        } else {
            tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.invalid").setStyle(red));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote && player.isSneaking()) {
            return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(RSItems.PATTERN, player.getHeldItem(hand).getCount()));
        }

        return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
    }

    @Override
    @Nonnull
    public ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container) {
        return new CraftingPattern(world, container, stack);
    }

    public static void setInputSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_INPUT_SLOT, slot), stack.serializeNBT());
    }

    @Nonnull
    public static ItemStack getInputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_INPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.read(pattern.getTag().getCompound(id));
    }

    public static void setOutputSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_OUTPUT_SLOT, slot), stack.serializeNBT());
    }

    @Nonnull
    public static ItemStack getOutputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_OUTPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.read(pattern.getTag().getCompound(id));
    }

    public static void setFluidInputSlot(ItemStack pattern, int slot, FluidStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_FLUID_INPUT_SLOT, slot), stack.writeToNBT(new CompoundNBT()));
    }

    public static FluidStack getFluidInputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_FLUID_INPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return FluidStack.EMPTY;
        }

        return FluidStack.loadFluidStackFromNBT(pattern.getTag().getCompound(id));
    }

    public static void setFluidOutputSlot(ItemStack pattern, int slot, FluidStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_FLUID_OUTPUT_SLOT, slot), stack.writeToNBT(new CompoundNBT()));
    }

    public static FluidStack getFluidOutputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_FLUID_OUTPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return FluidStack.EMPTY;
        }

        return FluidStack.loadFluidStackFromNBT(pattern.getTag().getCompound(id));
    }

    public static boolean isProcessing(ItemStack pattern) {
        return pattern.hasTag() && pattern.getTag().contains(NBT_PROCESSING) && pattern.getTag().getBoolean(NBT_PROCESSING);
    }

    public static void setProcessing(ItemStack pattern, boolean processing) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().putBoolean(NBT_PROCESSING, processing);
    }

    public static boolean isExact(ItemStack pattern) {
        if (!pattern.hasTag() || !pattern.getTag().contains(NBT_EXACT)) {
            return true;
        }

        return pattern.getTag().getBoolean(NBT_EXACT);
    }

    public static void setExact(ItemStack pattern, boolean exact) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().putBoolean(NBT_EXACT, exact);
    }

    public static void setToCurrentVersion(ItemStack pattern) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().putInt(NBT_VERSION, VERSION);
    }
}
