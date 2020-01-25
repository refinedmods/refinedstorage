package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.v5;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

class Crafting {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_TOOK = "Took";
    private static final String NBT_TO_EXTRACT = "ToExtract";
    private static final String NBT_ROOT = "Root";

    private ICraftingPattern pattern;
    private NonNullList<ItemStack> took;
    private IStackList<ItemStack> toExtract;
    private boolean root;

    public Crafting(ICraftingPattern pattern, NonNullList<ItemStack> took, IStackList<ItemStack> toExtract, boolean root) {
        this.pattern = pattern;
        this.took = took;
        this.toExtract = toExtract;
        this.root = root;
    }

    public Crafting(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.toExtract = CraftingTask.readItemStackList(tag.getList(NBT_TO_EXTRACT, Constants.NBT.TAG_COMPOUND));
        this.root = tag.getBoolean(NBT_ROOT);

        this.took = NonNullList.create();

        ListNBT tookList = tag.getList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompound(i));

            // Can be empty.
            took.add(stack);
        }
    }

    public boolean isRoot() {
        return root;
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

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();

        tag.put(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.put(NBT_TO_EXTRACT, CraftingTask.writeItemStackList(toExtract));
        tag.putBoolean(NBT_ROOT, root);

        ListNBT tookList = new ListNBT();
        for (ItemStack took : this.took) {
            tookList.add(StackUtils.serializeStackToNbt(took));
        }

        tag.put(NBT_TOOK, tookList);

        return tag;
    }
}
