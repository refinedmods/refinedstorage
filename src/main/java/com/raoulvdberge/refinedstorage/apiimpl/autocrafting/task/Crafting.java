package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

class Crafting {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_TOOK = "Took";
    private static final String NBT_TO_EXTRACT = "ToExtract";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_CONTAINERS = "Containers";
    private static final String NBT_QUANTITY = "Quantity";

    private ICraftingPattern pattern;
    private NonNullList<ItemStack> took;
    private IStackList<ItemStack> toExtract;
    private List<ICraftingPatternContainer> containers = new ArrayList<>();
    private int quantity;
    private boolean root;

    public Crafting(ICraftingPattern pattern, int quantity, NonNullList<ItemStack> took, IStackList<ItemStack> toExtract, boolean root) {
        this.pattern = pattern;
        this.took = took;
        this.toExtract = toExtract;
        this.root = root;
        this.containers.add(pattern.getContainer());
        this.quantity = quantity;
    }

    public Crafting(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.toExtract = CraftingTask.readItemStackList(tag.getTagList(NBT_TO_EXTRACT, Constants.NBT.TAG_COMPOUND));
        this.root = tag.getBoolean(NBT_ROOT);

        this.took = NonNullList.create();

        NBTTagList tookList = tag.getTagList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.tagCount(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompoundTagAt(i));

            // Can be empty.
            took.add(stack);
        }
        this.quantity = tag.getInteger(NBT_QUANTITY);
        this.containers = CraftingTask.readContainerList(tag.getTagList(NBT_CONTAINERS, Constants.NBT.TAG_COMPOUND), network.world());
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity() {
        quantity--;
    }

    public void addContainer(ICraftingPatternContainer container) {
        containers.add(container);
    }

    public List<ICraftingPatternContainer> getContainer() {
        return containers;
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

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.setTag(NBT_TO_EXTRACT, CraftingTask.writeItemStackList(toExtract));
        tag.setBoolean(NBT_ROOT, root);

        NBTTagList tookList = new NBTTagList();
        for (ItemStack took : this.took) {
            tookList.appendTag(StackUtils.serializeStackToNbt(took));
        }

        tag.setTag(NBT_TOOK, tookList);
        tag.setInteger(NBT_QUANTITY, quantity);
        tag.setTag(NBT_CONTAINERS, CraftingTask.writeContainerList(containers));

        return tag;
    }
}
