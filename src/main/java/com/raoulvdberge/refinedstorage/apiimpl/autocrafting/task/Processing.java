package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

class Processing extends Craft {
    private static final String NBT_ITEMS_TO_RECEIVE = "ItemsToReceive";
    private static final String NBT_FLUIDS_TO_RECEIVE = "FluidsToReceive";
    private static final String NBT_FLUIDS_TO_PUT = "FluidsToPut";
    private static final String NBT_STATE = "State";

    private IStackList<ItemStack> itemsToReceive;
    private IStackList<FluidStack> fluidsToReceive;
    private IStackList<FluidStack> fluidsToPut;
    private ProcessingState state = ProcessingState.READY;

    public Processing(ICraftingPattern pattern, IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, IStackList<ItemStack> itemsToUse, IStackList<FluidStack> fluidsToPut, boolean root) {
        super(pattern, root, itemsToUse);
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.fluidsToPut = fluidsToPut;
    }

    public Processing(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.itemsToReceive = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToPut = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.state = ProcessingState.values()[tag.getInt(NBT_STATE)];
    }


    public IStackList<ItemStack> getItemsToReceive() {
        return itemsToReceive;
    }

    public IStackList<FluidStack> getFluidsToReceive() {
        return fluidsToReceive;
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

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();
        tag.put(NBT_ITEMS_TO_RECEIVE, CraftingTask.writeItemStackList(itemsToReceive));
        tag.put(NBT_FLUIDS_TO_RECEIVE, CraftingTask.writeFluidStackList(fluidsToReceive));
        tag.put(NBT_FLUIDS_TO_PUT, CraftingTask.writeFluidStackList(fluidsToPut));
        tag.putInt(NBT_STATE, state.ordinal());

        return tag;
    }
}
