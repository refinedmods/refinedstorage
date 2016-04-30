package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class ItemPattern extends ItemBase {
    public static final String NBT_RESULT = "Result";
    public static final String NBT_INGREDIENTS = "Ingredients";
    public static final String NBT_PROCESSING = "Processing";

    public ItemPattern() {
        super("pattern");
    }

    @Override
    public void addInformation(ItemStack pattern, EntityPlayer player, List list, boolean b) {
        if (hasResult(pattern)) {
            list.add(getResult(pattern).getDisplayName());

            if (isProcessing(pattern)) {
                list.add(TextFormatting.ITALIC + I18n.translateToLocal("misc.refinedstorage:processing") + TextFormatting.RESET);
            }
        }
    }

    public static void addIngredient(ItemStack pattern, ItemStack stack) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        if (!pattern.getTagCompound().hasKey(NBT_INGREDIENTS)) {
            pattern.getTagCompound().setTag(NBT_INGREDIENTS, new NBTTagList());
        }

        pattern.getTagCompound().getTagList(NBT_INGREDIENTS, Constants.NBT.TAG_COMPOUND).appendTag(stack.serializeNBT());
    }

    public static ItemStack[] getIngredients(ItemStack pattern) {
        if (pattern.getTagCompound() == null) {
            return null;
        }

        if (!pattern.getTagCompound().hasKey(NBT_INGREDIENTS)) {
            return null;
        }

        NBTTagList ingredients = pattern.getTagCompound().getTagList(NBT_INGREDIENTS, Constants.NBT.TAG_COMPOUND);

        ItemStack[] ingredientsArray = new ItemStack[ingredients.tagCount()];

        for (int i = 0; i < ingredients.tagCount(); ++i) {
            ingredientsArray[i] = ItemStack.loadItemStackFromNBT(ingredients.getCompoundTagAt(i));
        }

        return ingredientsArray;
    }

    public static void setResult(ItemStack pattern, ItemStack stack) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound stackTag = new NBTTagCompound();
        stack.writeToNBT(stackTag);

        pattern.getTagCompound().setTag(NBT_RESULT, stackTag);
    }

    public static void setProcessing(ItemStack pattern, boolean processing) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        pattern.getTagCompound().setBoolean(NBT_PROCESSING, processing);
    }

    public static boolean isProcessing(ItemStack pattern) {
        if (pattern.getTagCompound() == null) {
            return false;
        }

        return pattern.getTagCompound().getBoolean(NBT_PROCESSING);
    }

    public static boolean hasResult(ItemStack pattern) {
        if (pattern.getTagCompound() == null) {
            return false;
        }

        return pattern.getTagCompound().hasKey(NBT_RESULT);
    }

    public static ItemStack getResult(ItemStack pattern) {
        if (!hasResult(pattern)) {
            return null;
        }

        return ItemStack.loadItemStackFromNBT(pattern.getTagCompound().getCompoundTag(NBT_RESULT));
    }
}
