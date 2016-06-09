package refinedstorage.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import refinedstorage.RefinedStorageUtils;

import java.util.List;

public class ItemPattern extends ItemBase {
    public static final String NBT_INPUTS = "Inputs";
    public static final String NBT_OUTPUTS = "Outputs";
    public static final String NBT_BYPRODUCTS = "Byproducts";
    public static final String NBT_PROCESSING = "Processing";

    public ItemPattern() {
        super("pattern");
    }

    @Override
    public void addInformation(ItemStack pattern, EntityPlayer player, List list, boolean b) {
        if (isValid(pattern)) {
            if (GuiScreen.isShiftKeyDown() || isProcessing(pattern)) {
                list.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.inputs") + TextFormatting.RESET);

                RefinedStorageUtils.combineMultipleItemsInTooltip(list, getInputs(pattern));

                list.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.outputs") + TextFormatting.RESET);
            }

            RefinedStorageUtils.combineMultipleItemsInTooltip(list, getOutputs(pattern));
        }
    }

    public static void addInput(ItemStack pattern, ItemStack stack) {
        add(pattern, stack, NBT_INPUTS);
    }

    public static void addOutput(ItemStack pattern, ItemStack stack) {
        add(pattern, stack, NBT_OUTPUTS);
    }

    public static void addByproduct(ItemStack pattern, ItemStack stack) {
        add(pattern, stack, NBT_BYPRODUCTS);
    }

    private static void add(ItemStack pattern, ItemStack stack, String type) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        if (!pattern.getTagCompound().hasKey(type)) {
            pattern.getTagCompound().setTag(type, new NBTTagList());
        }

        pattern.getTagCompound().getTagList(type, Constants.NBT.TAG_COMPOUND).appendTag(stack.serializeNBT());
    }

    public static ItemStack[] getInputs(ItemStack pattern) {
        return get(pattern, NBT_INPUTS);
    }

    public static ItemStack[] getOutputs(ItemStack pattern) {
        return get(pattern, NBT_OUTPUTS);
    }

    public static ItemStack[] getByproducts(ItemStack pattern) {
        return get(pattern, NBT_BYPRODUCTS);
    }

    private static ItemStack[] get(ItemStack pattern, String type) {
        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(type)) {
            return null;
        }

        NBTTagList stacksList = pattern.getTagCompound().getTagList(type, Constants.NBT.TAG_COMPOUND);

        ItemStack[] stacks = new ItemStack[stacksList.tagCount()];

        for (int i = 0; i < stacksList.tagCount(); ++i) {
            stacks[i] = ItemStack.loadItemStackFromNBT(stacksList.getCompoundTagAt(i));
        }

        return stacks;
    }

    public static boolean isValid(ItemStack pattern) {
        if (pattern.getTagCompound() == null || (!pattern.getTagCompound().hasKey(NBT_INPUTS) || !pattern.getTagCompound().hasKey(NBT_OUTPUTS) || !pattern.getTagCompound().hasKey(NBT_PROCESSING))) {
            return false;
        }

        for (ItemStack input : getInputs(pattern)) {
            if (input == null) {
                return false;
            }
        }

        for (ItemStack output : getOutputs(pattern)) {
            if (output == null) {
                return false;
            }
        }

        ItemStack[] byproducts = getByproducts(pattern);
        if (byproducts != null) {
            for (ItemStack byproduct : byproducts) {
                if (byproduct == null) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void setProcessing(ItemStack pattern, boolean processing) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_PROCESSING, processing);
    }

    public static boolean isProcessing(ItemStack pattern) {
        if (!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(NBT_PROCESSING)) {
            return false;
        }

        return pattern.getTagCompound().getBoolean(NBT_PROCESSING);
    }
}
