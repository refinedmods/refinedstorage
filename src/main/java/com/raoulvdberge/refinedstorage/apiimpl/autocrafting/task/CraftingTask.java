package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.List;

public class CraftingTask implements ICraftingTask {
    private INetwork network;
    private ItemStack stack;
    private int quantity;
    private ICraftingPattern pattern;

    public CraftingTask(INetwork network, ItemStack stack, int quantity, ICraftingPattern pattern) {
        this.network = network;
        this.stack = stack;
        this.quantity = quantity;
        this.pattern = pattern;
    }

    @Override
    public void calculate() {
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public void onCancelled() {
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public ItemStack getRequested() {
        return stack;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        return tag;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        return Collections.singletonList(new CraftingMonitorElementItemRender(network.getCraftingManager().getTasks().indexOf(this), stack, quantity, 0));
    }

    @Override
    public List<ICraftingPreviewElement> getPreviewStacks() {
        return Collections.emptyList();
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
