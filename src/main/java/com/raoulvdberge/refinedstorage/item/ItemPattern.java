package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.util.StackUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ItemPattern extends ItemBase implements ICraftingPatternProvider {
    private static Map<ItemStack, CraftingPattern> PATTERN_CACHE = new HashMap<>();

    private static final String NBT_SLOT = "Slot_%d";
    private static final String NBT_OUTPUTS = "Outputs";
    private static final String NBT_OREDICT = "Oredict";
    private static final String NBT_BLOCKING = "Blocking";

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

                combineItems(tooltip, true, StackUtils.toNonNullList(pattern.getInputs()));

                tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.outputs") + TextFormatting.RESET);
            }

            combineItems(tooltip, true, StackUtils.toNonNullList(pattern.getOutputs()));

            if (isOredict(stack)) {
                tooltip.add(TextFormatting.BLUE + I18n.format("misc.refinedstorage:pattern.oredict") + TextFormatting.RESET);
            }

            if (isBlocking(stack)) {
                tooltip.add(TextFormatting.BLUE + I18n.format("misc.refinedstorage:blocking") + TextFormatting.RESET);
            }
        } else {
            tooltip.add(TextFormatting.RED + I18n.format("misc.refinedstorage:pattern.invalid") + TextFormatting.RESET);
        }
    }

    public static void setSlot(ItemStack pattern, int slot, ItemStack stack) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setTag(String.format(NBT_SLOT, slot), stack.serializeNBT());
    }

    public static ItemStack getSlot(ItemStack pattern, int slot) {
        String id = String.format(NBT_SLOT, slot);

        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
            return null;
        }

        return new ItemStack(pattern.getTagCompound().getCompoundTag(id));
    }

    public static void addOutput(ItemStack pattern, ItemStack output) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        NBTTagList outputs;
        if (!pattern.getTagCompound().hasKey(NBT_OUTPUTS)) {
            outputs = new NBTTagList();
        } else {
            outputs = pattern.getTagCompound().getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);
        }

        outputs.appendTag(output.serializeNBT());

        pattern.getTagCompound().setTag(NBT_OUTPUTS, outputs);
    }

    public static List<ItemStack> getOutputs(ItemStack pattern) {
        if (!isProcessing(pattern)) {
            return null;
        }

        IStackList<ItemStack> outputs = API.instance().createItemStackList();

        NBTTagList outputsTag = pattern.getTagCompound().getTagList(NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < outputsTag.tagCount(); ++i) {
            ItemStack stack = new ItemStack(outputsTag.getCompoundTagAt(i));

            if (!stack.isEmpty()) {
                outputs.add(stack);
            }
        }

        return new ArrayList<>(outputs.getStacks());
    }

    public static boolean isProcessing(ItemStack pattern) {
        return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_OUTPUTS);
    }

    public static boolean isOredict(ItemStack pattern) {
        return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_OREDICT) && pattern.getTagCompound().getBoolean(NBT_OREDICT);
    }

    public static boolean isBlocking(ItemStack pattern) {
        return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_BLOCKING) && pattern.getTagCompound().getBoolean(NBT_BLOCKING);
    }

    public static void setOredict(ItemStack pattern, boolean oredict) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_OREDICT, oredict);
    }

    public static void setBlocking(ItemStack pattern, boolean blockingTask) {
        if (!pattern.hasTagCompound()) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_BLOCKING, blockingTask);
    }

    public static void combineItems(List<String> tooltip, boolean displayAmount, NonNullList<ItemStack> stacks) {
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (!stacks.get(i).isEmpty() && !combinedIndices.contains(i)) {
                ItemStack stack = stacks.get(i);

                String data = stack.getDisplayName();

                int amount = stack.getCount();

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (API.instance().getComparer().isEqual(stack, stacks.get(j))) {
                        amount += stacks.get(j).getCount();

                        combinedIndices.add(j);
                    }
                }

                data = (displayAmount ? (TextFormatting.WHITE + String.valueOf(amount) + " ") : "") + TextFormatting.GRAY + data;

                tooltip.add(data);
            }
        }
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
}
