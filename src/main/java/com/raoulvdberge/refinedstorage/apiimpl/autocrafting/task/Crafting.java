package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

class Crafting {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_TOOK = "Took";
    private static final String NBT_TO_EXTRACT = "ToExtract";

    private ICraftingPattern pattern;
    private NonNullList<ItemStack> took;
    private IStackList<ItemStack> toExtract;

    public Crafting(ICraftingPattern pattern, NonNullList<ItemStack> took, IStackList<ItemStack> toExtract) {
        this.pattern = pattern;
        this.took = took;
        this.toExtract = toExtract;
    }

    public Crafting(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.toExtract = CraftingTask.readItemStackList(tag.getTagList(NBT_TO_EXTRACT, Constants.NBT.TAG_COMPOUND));

        this.took = NonNullList.create();

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.tagCount(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompoundTagAt(i));

            // Can be empty.
            took.add(stack);
        }
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public NonNullList<ItemStack> getTook() {
        return took;
    }

    public IStackList<ItemStack> getToExtract() {
        return toExtract;
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.setTag(NBT_TO_EXTRACT, CraftingTask.writeItemStackList(toExtract));

        NBTTagList tookList = new NBTTagList();
        for (ItemStack took : this.took) {
            tookList.appendTag(StackUtils.serializeStackToNbt(took));
        }

        tag.setTag(NBT_TOOK, tookList);

        return tag;
    }
}
