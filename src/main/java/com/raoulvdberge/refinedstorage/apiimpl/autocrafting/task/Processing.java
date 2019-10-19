package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

class Processing {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ITEMS_TO_RECEIVE = "ItemsToReceive";
    private static final String NBT_FLUIDS_TO_RECEIVE = "FluidsToReceive";
    private static final String NBT_ITEMS_TO_PUT = "ItemsToPut";
    private static final String NBT_FLUIDS_TO_PUT = "FluidsToPut";
    private static final String NBT_STATE = "State";
    private static final String NBT_ROOT = "Root";

    private ICraftingPattern pattern;
    private IStackList<ItemStack> itemsToReceive;
    private IStackList<FluidStack> fluidsToReceive;
    private IStackList<ItemStack> itemsToPut;
    private IStackList<FluidStack> fluidsToPut;
    private ProcessingState state = ProcessingState.READY;
    private boolean root;

    public Processing(ICraftingPattern pattern, IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, IStackList<ItemStack> itemsToPut, IStackList<FluidStack> fluidsToPut, boolean root) {
        this.pattern = pattern;
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.itemsToPut = itemsToPut;
        this.fluidsToPut = fluidsToPut;
        this.root = root;
    }

    public Processing(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.itemsToReceive = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.root = tag.getBoolean(NBT_ROOT);
        this.itemsToPut = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.fluidsToPut = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.state = ProcessingState.values()[tag.getInt(NBT_STATE)];
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

    public IStackList<ItemStack> getItemsToPut() {
        return itemsToPut;
    }

    public IStackList<FluidStack> getFluidsToPut() {
        return fluidsToPut;
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public ProcessingState getState() {
        return state;
    }

    public boolean isRoot() {
        return root;
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();

        tag.put(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.put(NBT_ITEMS_TO_RECEIVE, CraftingTask.writeItemStackList(itemsToReceive));
        tag.put(NBT_FLUIDS_TO_RECEIVE, CraftingTask.writeFluidStackList(fluidsToReceive));
        tag.putBoolean(NBT_ROOT, root);
        tag.put(NBT_ITEMS_TO_PUT, CraftingTask.writeItemStackList(itemsToPut));
        tag.put(NBT_FLUIDS_TO_PUT, CraftingTask.writeFluidStackList(fluidsToPut));
        tag.putInt(NBT_STATE, state.ordinal());

        return tag;
    }
}
