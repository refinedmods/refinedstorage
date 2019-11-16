package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.api.util.StackListEntry;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

class Processing extends Craft {
    private static final String NBT_ITEMS_TO_RECEIVE = "ItemsToReceive";
    private static final String NBT_FLUIDS_TO_RECEIVE = "FluidsToReceive";
    private static final String NBT_FLUIDS_TO_PUT = "FluidsToPut";
    private static final String NBT_STATE = "State";
    private static final String NBT_QUANTITY_TOTAL = "TotalQuantity";
    private static final String NBT_ITEMS_RECEIVED = "ItemsReceived";
    private static final String NBT_FLUIDS_RECEIVED = "FluidsReceived";

    private IStackList<ItemStack> itemsToReceive;
    private IStackList<ItemStack> itemsReceived = API.instance().createItemStackList();
    private IStackList<FluidStack> fluidsToReceive;
    private IStackList<FluidStack> fluidsReceived = API.instance().createFluidStackList();
    private IStackList<FluidStack> fluidsToPut;
    private ProcessingState state = ProcessingState.READY;

    private int finished;
    private int totalQuantity;

    public Processing(int quantity, ICraftingPattern pattern, IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, IStackList<ItemStack> itemsToUse, IStackList<FluidStack> fluidsToPut, boolean root) {
        super(quantity, pattern, root, itemsToUse);
        this.totalQuantity = quantity;
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
        this.totalQuantity = tag.getInt(NBT_QUANTITY_TOTAL);
        this.itemsReceived = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.fluidsReceived = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_RECEIVED, Constants.NBT.TAG_COMPOUND));
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

    int getNeeded(ItemStack stack) {
        if (itemsToReceive.get(stack) != null) {
            int needed = itemsToReceive.get(stack).getCount() * totalQuantity;
            if (itemsReceived.get(stack) != null) {
                needed -= itemsReceived.get(stack).getCount();
            }
            return needed;
        }
        return 0;
    }

    int getNeeded(FluidStack stack) {
        if (fluidsToReceive.get(stack) != null) {
            int needed = fluidsToReceive.get(stack).getAmount() * totalQuantity;
            if (fluidsReceived.get(stack) != null) {
                needed -= fluidsReceived.get(stack).getAmount();
            }
            return needed;
        }
        return 0;
    }

    public int getProcessing() {
        return totalQuantity - quantity - finished;
    }

    public int getScheduled() {
        return totalQuantity - finished;
    }

    void addFinished(ItemStack received, int size) {
        itemsReceived.add(received, size);

    }

    void addFinished(FluidStack received, int size) {
        fluidsReceived.add(received, size);
    }

    boolean updateFinished() {
        int fin = finished;
        updateFinishedPatterns();
        if (finished == totalQuantity) {
            this.setState(ProcessingState.PROCESSED);
        }
        return fin == finished;
    }

    /*
       Calculates how many patterns were already finished
       by calculating the number finished patterns for every output
       and then taking the minimum of those
    */
    private void updateFinishedPatterns() {
        int temp = totalQuantity;
        if (!itemsToReceive.isEmpty()) {
            for (StackListEntry<ItemStack> stack : itemsToReceive.getStacks()) {
                if (itemsReceived.get(stack.getStack()) != null) {
                    if (temp > itemsReceived.get(stack.getStack()).getCount() / (itemsToReceive.get(stack.getStack()).getCount())) {
                        temp = itemsReceived.get(stack.getStack()).getCount() / (itemsToReceive.get(stack.getStack()).getCount());
                    }
                } else {
                    temp = 0;
                }
            }
        }
        if (!fluidsToReceive.isEmpty()) {
            for (StackListEntry<FluidStack> stack : fluidsToReceive.getStacks()) {
                if (fluidsReceived.get(stack.getStack()) != null) {
                    if (temp > fluidsReceived.get(stack.getStack()).getAmount() / (fluidsToReceive.get(stack.getStack()).getAmount())) {
                        temp = fluidsReceived.get(stack.getStack()).getAmount() / (fluidsToReceive.get(stack.getStack()).getAmount());
                    }
                } else {
                    temp = 0;
                }
            }
        }
        finished = temp;
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
        tag.putInt(NBT_QUANTITY_TOTAL, totalQuantity);
        tag.put(NBT_ITEMS_RECEIVED, CraftingTask.writeItemStackList(itemsReceived));
        tag.put(NBT_FLUIDS_RECEIVED, CraftingTask.writeFluidStackList(fluidsReceived));

        return tag;
    }
}
