package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
    private static final String NBT_INSERTED = "Inserted";


    private IStackList<ItemStack> itemsToReceive;
    private IStackList<FluidStack> fluidsToReceive;
    private IStackList<FluidStack> fluidsToPut;
    private IStackList<ItemStack> itemsReceived = API.instance().createItemStackList();
    private IStackList<FluidStack> fluidsReceived = API.instance().createFluidStackList();
    private IStackList<ItemStack> itemsToReceiveTotal;
    private IStackList<FluidStack> fluidsToReceiveTotal;

    private ProcessingState state = ProcessingState.READY_OR_PROCESSING;

    private int totalQuantity;
    private int finished;
    private int inserted;
    boolean isInitialized;

    Processing(ICraftingPattern pattern, boolean root) {
        super(pattern, root);
    }

    void initialize(IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, IStackList<FluidStack> fluidsToPut) {
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.fluidsToPut = fluidsToPut;
        isInitialized = true;
    }

    Processing(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        super(network, tag);
        this.itemsToReceive = CraftingTask.readItemStackList(tag.getTagList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToPut = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.state = ProcessingState.values()[tag.getInteger(NBT_STATE)];
        this.totalQuantity = tag.getInteger(NBT_QUANTITY_TOTAL);
        this.itemsReceived = CraftingTask.readItemStackList(tag.getTagList(NBT_ITEMS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.fluidsReceived = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.inserted = tag.getInteger(NBT_INSERTED);
        setTotals();
    }

    @Override
    public void finishCalculation() {
        super.finishCalculation();
        totalQuantity = getQuantity();
        setTotals();
    }

    private void setTotals() {
        itemsToReceiveTotal = itemsToReceive.copy();
        itemsToReceiveTotal.getStacks().forEach(x -> x.setCount(x.getCount() * totalQuantity));
        fluidsToReceiveTotal = fluidsToReceive.copy();
        fluidsToReceiveTotal.getStacks().forEach(x -> x.amount *= totalQuantity);

    }

    @Override
    public void nextSet() {
        super.nextSet();
        inserted++;
    }

    boolean isNothingProcessing() {
        return (totalQuantity - getQuantity()) == finished; // no items are in processing
    }

    int getScheduled(ItemStack stack) {
        if (itemsToReceiveTotal.get(stack) != null) {
            int scheduled = itemsToReceiveTotal.get(stack).getCount();
            if (itemsReceived.get(stack) != null) {
                scheduled -= itemsReceived.get(stack).getCount();
            }
            return scheduled;
        }
        return 0;
    }

    int getScheduled(FluidStack stack) {
        if (fluidsToReceiveTotal.get(stack) != null) {
            int scheduled = fluidsToReceiveTotal.get(stack).amount;
            if (fluidsReceived.get(stack) != null) {
                scheduled -= fluidsReceived.get(stack).amount;
            }
            return scheduled;
        }
        return 0;
    }

    public int getInserted() {
        return inserted - finished;
    }


    boolean calculateFinished(ItemStack received, int size) {
        itemsReceived.add(received, size);
        return isFinished();
    }

    boolean calculateFinished(FluidStack received, int size) {
        fluidsReceived.add(received, size);
        return isFinished();
    }

    private boolean isFinished() {
        updateFinishedPatterns();
        return finished == totalQuantity;
    }

    /*
       Calculates how many patterns were already finished
       by calculating the number finished patterns for every output
       and then taking the minimum of those
    */
    private void updateFinishedPatterns() {
        int temp = totalQuantity;
        if (!itemsToReceive.isEmpty()) {
            for (ItemStack stack : itemsToReceive.getStacks()) {
                if (itemsReceived.get(stack) != null) {
                    if (temp > itemsReceived.get(stack).getCount() / itemsToReceive.get(stack).getCount()) {
                        temp = itemsReceived.get(stack).getCount() / itemsToReceive.get(stack).getCount();
                    }
                } else {
                    temp = 0;
                }
            }
        }
        if (!fluidsToReceive.isEmpty()) {
            for (FluidStack stack : fluidsToReceive.getStacks()) {
                if (fluidsReceived.get(stack) != null) {
                    if (temp > fluidsReceived.get(stack).amount / fluidsToReceive.get(stack).amount) {
                        temp = fluidsReceived.get(stack).amount / fluidsToReceive.get(stack).amount;
                    }
                } else {
                    temp = 0;
                }
            }
        }
        finished = temp;
    }

    IStackList<FluidStack> getFluidsToPut() {
        return fluidsToPut;
    }

    IStackList<ItemStack> getItemsToReceiveTotal() {
        return itemsToReceiveTotal;
    }

    IStackList<FluidStack> getFluidsToReceiveTotal() {
        return fluidsToReceiveTotal;
    }

    int getFluidReceivedCount(FluidStack stack) {
        if (fluidsReceived.get(stack) != null) {
            return fluidsReceived.get(stack).amount;
        }
        return 0;
    }

    int getItemReceivedCount(ItemStack stack) {
        if (itemsReceived.get(stack) != null) {
            return itemsReceived.get(stack).getCount();
        }
        return 0;
    }


    public void setState(ProcessingState state) {
        this.state = state;
    }

    public ProcessingState getState() {
        return state;
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = super.writeToNbt();

        tag.setTag(NBT_ITEMS_TO_RECEIVE, CraftingTask.writeItemStackList(itemsToReceive));
        tag.setTag(NBT_FLUIDS_TO_RECEIVE, CraftingTask.writeFluidStackList(fluidsToReceive));
        tag.setTag(NBT_FLUIDS_TO_PUT, CraftingTask.writeFluidStackList(fluidsToPut));
        tag.setInteger(NBT_STATE, state.ordinal());
        tag.setInteger(NBT_QUANTITY_TOTAL, totalQuantity);
        tag.setTag(NBT_ITEMS_RECEIVED, CraftingTask.writeItemStackList(itemsReceived));
        tag.setTag(NBT_FLUIDS_RECEIVED, CraftingTask.writeFluidStackList(fluidsReceived));
        tag.setInteger(NBT_INSERTED, inserted);

        return tag;
    }
}
