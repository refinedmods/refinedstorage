package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public abstract class Craft {
     static final String NBT_PATTERN = "Pattern";
     static final String NBT_ROOT = "Root";
     static final String NBT_QUANTITY = "Quantity";
     static final String NBT_CONTAINERS = "Containers";

     int quantity = 0;
     boolean isProcessing, root, isInitialized;

     ICraftingPattern pattern;
     List<ICraftingPatternContainer> containers = new ArrayList<>();

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
    public void reduceQuantity() {
        quantity--;
    }
    public int getQuantity() {
        return quantity;
    }
    public void addContainer(ICraftingPatternContainer container) {
        containers.add(container);
    }
    public List<ICraftingPatternContainer> getContainer() {
        return containers;
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }
    public boolean isRoot() {
        return root;
    }
    public NBTTagCompound writeToNbt(){
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(NBT_ROOT, root);
        tag.setInteger(NBT_QUANTITY, quantity);
        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.setTag(NBT_CONTAINERS, CraftingTask.writeContainerList(containers));
        return tag;
    }
    public void finishCalculation() {};

}
