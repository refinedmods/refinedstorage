package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemPattern extends ItemBase implements ICraftingPatternProvider {
    private static Map<ItemStack, CraftingPattern> PATTERN_CACHE = new HashMap<>();

    public static final String NBT_INPUT_SLOT = "Input_%d";
    public static final String NBT_OUTPUT_SLOT = "Output_%d";
    private static final String NBT_OREDICT = "Oredict";
    public static final String NBT_PROCESSING = "Processing";

    public ItemPattern() {
        super("pattern");
    }

    public static CraftingPattern getPatternFromCache(World world, ItemStack stack) {
        if (!PATTERN_CACHE.containsKey(stack)) {
            PATTERN_CACHE.put(stack, new CraftingPattern(world, null, stack));
        }

        return PATTERN_CACHE.get(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (!stack.hasTagCompound()) {
            return;
        }

        ICraftingPattern pattern = getPatternFromCache(world, stack);

        if (pattern.isValid()) {
            if (GuiScreen.isShiftKeyDown() || isProcessing(stack)) {
                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.inputs") + TextFormatting.RESET);

                RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getInputs().stream().map(i -> i.size() > 0 ? i.get(0) : ItemStack.EMPTY).collect(Collectors.toList()));

                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.outputs") + TextFormatting.RESET);
            }

            RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getOutputs());

            if (isOredict(stack)) {
                tooltip.add(TextFormatting.BLUE + I18n.format("misc.refinedstorage:pattern.oredict") + TextFormatting.RESET);
            }

            if (isProcessing(stack)) {
                tooltip.add(TextFormatting.BLUE + I18n.format("misc.refinedstorage:processing") + TextFormatting.RESET);
            }
        } else {
            tooltip.add(TextFormatting.RED + I18n.format("misc.refinedstorage:pattern.invalid") + TextFormatting.RESET);
        }
    }

    public static void setInputSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setTag(String.format(NBT_INPUT_SLOT, slot), stack.serializeNBT());
    }

    @Nullable
    public static ItemStack getInputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_INPUT_SLOT, slot);

        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
            return null;
        }

        ItemStack stack = new ItemStack(pattern.getTagCompound().getCompoundTag(id));
        if (stack.isEmpty()) {
            return null;
        }

        return stack;
    }

    public static void setOutputSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setTag(String.format(NBT_OUTPUT_SLOT, slot), stack.serializeNBT());
    }

    @Nullable
    public static ItemStack getOutputSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_OUTPUT_SLOT, slot);

        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
            return null;
        }

        ItemStack stack = new ItemStack(pattern.getTagCompound().getCompoundTag(id));
        if (stack.isEmpty()) {
            return null;
        }

        return stack;
    }

    public static boolean isProcessing(ItemStack pattern) {
        return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_PROCESSING) && pattern.getTagCompound().getBoolean(NBT_PROCESSING);
    }

    public static void setProcessing(ItemStack pattern, boolean processing) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_PROCESSING, processing);
    }

    public static boolean isOredict(ItemStack pattern) {
        return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_OREDICT) && pattern.getTagCompound().getBoolean(NBT_OREDICT);
    }

    public static void setOredict(ItemStack pattern, boolean oredict) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_OREDICT, oredict);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(RSItems.PATTERN, player.getHeldItem(hand).getCount()));
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    @Override
    @Nonnull
    public ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container) {
        return new CraftingPattern(world, container, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        super.onUpdate(stack, world, entity, slot, isSelected);

        if (!world.isRemote) {
            OneSixMigrationHelper.migratePattern(stack);
        }
    }
}
