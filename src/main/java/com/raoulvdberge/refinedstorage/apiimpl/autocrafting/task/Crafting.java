package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

class Crafting {
    private ICraftingPattern pattern;
    private NonNullList<ItemStack> took;
    private IStackList<ItemStack> toExtract;

    public Crafting(ICraftingPattern pattern, NonNullList<ItemStack> took, IStackList<ItemStack> toExtract) {
        this.pattern = pattern;
        this.took = took;
        this.toExtract = toExtract;
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
}
