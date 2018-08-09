package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

class Processing {
    private ICraftingPattern pattern;
    private IStackList<ItemStack> itemsToReceive;
    private IStackList<FluidStack> fluidsToReceive;
    private List<ItemStack> itemsToPut;
    private List<FluidStack> fluidsToPut;
    private ProcessingState state = ProcessingState.READY;

    public Processing(ICraftingPattern pattern, IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, List<ItemStack> itemsToPut, List<FluidStack> fluidsToPut) {
        this.pattern = pattern;
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.itemsToPut = itemsToPut;
        this.fluidsToPut = fluidsToPut;
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public IStackList<ItemStack> getItemsToReceive() {
        return itemsToReceive;
    }

    public IStackList<FluidStack> getFluidsToReceive() {
        return fluidsToReceive;
    }

    public List<ItemStack> getItemsToPut() {
        return itemsToPut;
    }

    public List<FluidStack> getFluidsToPut() {
        return fluidsToPut;
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public ProcessingState getState() {
        return state;
    }
}
