package refinedstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ItemPattern extends ItemBase {
    public static final String NBT_SLOT = "Slot_%d";
    public static final String NBT_RESULT = "Result";

    public ItemPattern() {
        super("pattern");
    }

    @Override
    public void addInformation(ItemStack pattern, EntityPlayer player, List list, boolean b) {
        if (getResult(pattern) != null) {
            list.add(getResult(pattern).getDisplayName());
        }
    }

    public static void setSlot(ItemStack pattern, int i, ItemStack stack) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound stackTag = new NBTTagCompound();
        stack.writeToNBT(stackTag);

        pattern.getTagCompound().setTag(String.format(NBT_SLOT, i), stackTag);
    }

    public static void setResult(ItemStack pattern, ItemStack stack) {
        if (pattern.getTagCompound() == null) {
            pattern.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound stackTag = new NBTTagCompound();
        stack.writeToNBT(stackTag);

        pattern.getTagCompound().setTag(NBT_RESULT, stackTag);
    }

    public static ItemStack getResult(ItemStack pattern) {
        if (pattern.getTagCompound() == null || !pattern.getTagCompound().hasKey(NBT_RESULT)) {
            return null;
        }

        return ItemStack.loadItemStackFromNBT(pattern.getTagCompound().getCompoundTag(NBT_RESULT));
    }
}
