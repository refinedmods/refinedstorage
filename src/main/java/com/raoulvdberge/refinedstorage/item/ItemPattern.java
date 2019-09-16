package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
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

public class ItemPattern extends Item implements ICraftingPatternProvider {
    private static Map<ItemStack, CraftingPattern> CACHE = new HashMap<>();

    private static final String NBT_VERSION = "Version";
    private static final String NBT_INPUT_SLOT = "Input_%d";
    private static final String NBT_OUTPUT_SLOT = "Output_%d";
    private static final String NBT_FLUID_INPUT_SLOT = "FluidInput_%d";
    private static final String NBT_FLUID_OUTPUT_SLOT = "FluidOutput_%d";
    private static final String NBT_OREDICT = "Oredict"; // TODO - Rename since oredict is gone
    private static final String NBT_PROCESSING = "Processing";

    private static final int VERSION = 1;

    public ItemPattern() {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, "pattern");
    }

    /* TODO - Pattern rendering
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));

        modelRegistration.addBakedModelOverride(info.getId(), BakedModelPattern::new);

        modelRegistration.addItemColor(this, new ItemColorPattern());
    }*/

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

            if (isOredict(stack)) {
                tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.oredict").setStyle(blue));
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

    @Nullable
    public static ItemStack getInputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_INPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return null;
        }

        ItemStack stack = ItemStack.read(pattern.getTag().getCompound(id));
        if (stack.isEmpty()) {
            return null;
        }

        return stack;
    }

    public static void setOutputSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_OUTPUT_SLOT, slot), stack.serializeNBT());
    }

    @Nullable
    public static ItemStack getOutputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_OUTPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return null;
        }

        ItemStack stack = ItemStack.read(pattern.getTag().getCompound(id));
        if (stack.isEmpty()) {
            return null;
        }

        return stack;
    }

    public static void setFluidInputSlot(ItemStack pattern, int slot, FluidStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_FLUID_INPUT_SLOT, slot), stack.writeToNBT(new CompoundNBT()));
    }

    @Nullable
    public static FluidStack getFluidInputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_FLUID_INPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return null;
        }

        return FluidStack.loadFluidStackFromNBT(pattern.getTag().getCompound(id));
    }

    public static void setFluidOutputSlot(ItemStack pattern, int slot, FluidStack stack) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().put(String.format(NBT_FLUID_OUTPUT_SLOT, slot), stack.writeToNBT(new CompoundNBT()));
    }

    @Nullable
    public static FluidStack getFluidOutputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_FLUID_OUTPUT_SLOT, slot);

        if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
            return null;
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

    public static boolean isOredict(ItemStack pattern) {
        return pattern.hasTag() && pattern.getTag().contains(NBT_OREDICT) && pattern.getTag().getBoolean(NBT_OREDICT);
    }

    public static void setOredict(ItemStack pattern, boolean oredict) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().putBoolean(NBT_OREDICT, oredict);
    }

    public static void setToCurrentVersion(ItemStack pattern) {
        if (!pattern.hasTag()) {
            pattern.setTag(new CompoundNBT());
        }

        pattern.getTag().putInt(NBT_VERSION, VERSION);
    }
}
