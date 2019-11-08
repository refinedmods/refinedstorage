package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.api.util.StackListEntry;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;


class Processing extends Craft {
    private static final String NBT_ITEMS_TO_RECEIVE = "ItemsToReceive";
    private static final String NBT_FLUIDS_TO_RECEIVE = "FluidsToReceive";
    private static final String NBT_FLUIDS_TO_PUT = "FluidsToPut";
    private static final String NBT_STATE = "State";
    private static final String NBT_QUANTITY_TOTAL = "TotalQuantity";
    private static final String NBT_ITEMS_RECEIVED = "ItemsReceived";
    private static final String NBT_FLUIDS_RECEIVED = "FluidsReceived";
    private static final String NBT_INSERTED = "Inserted";
    private static final String NBT_HAS_DUPLICATES = "HasDuplicates";


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
    private boolean hasDuplicates;

    Processing(ICraftingPattern pattern, boolean root) {
        super(pattern, root);
    }

    void initialize(IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, IStackList<FluidStack> fluidsToPut) {
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.fluidsToPut = fluidsToPut;
        isInitialized = true;
    }

    Processing(INetwork network,CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.itemsToReceive = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToPut = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.state = ProcessingState.values()[tag.getInt(NBT_STATE)];
        this.totalQuantity = tag.getInt(NBT_QUANTITY_TOTAL);
        this.itemsReceived = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.fluidsReceived = CraftingTask.readFluidStackList(tag.getList(NBT_FLUIDS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.inserted = tag.getInt(NBT_INSERTED);
        this.hasDuplicates = tag.getBoolean(NBT_HAS_DUPLICATES);
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
        itemsToReceiveTotal.getStacks().forEach(x -> x.getStack().setCount(x.getStack().getCount() * totalQuantity));
        fluidsToReceiveTotal = fluidsToReceive.copy();
        fluidsToReceiveTotal.getStacks().forEach(x -> x.getStack().setAmount(x.getStack().getAmount()* totalQuantity));

    }

    //Simulation of extraction can go wrong if the Collection has duplicates
    Collection<ItemStack> getNoDupeSet() {
        if (hasDuplicates) {
            Collection<ItemStack> list = super.getNextItemSet(Action.SIMULATE);
            Map<String, ItemStack> map = new HashMap<>();
            for (ItemStack stack : list) {
                if (map.containsKey(stack.getTranslationKey())) {
                    ItemStack in = map.get(stack.getTranslationKey());
                    ItemStack copy = ItemHandlerHelper.copyStackWithSize(in, in.getCount() + stack.getCount());
                    map.replace(stack.getTranslationKey(), copy);
                } else {
                    map.put(stack.getTranslationKey(), stack);
                }
            }
            return new LinkedHashSet<>(map.values());
        } else {
            return getNextItemSet(Action.SIMULATE);
        }
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
            int scheduled = fluidsToReceiveTotal.get(stack).getAmount();
            if (fluidsReceived.get(stack) != null) {
                scheduled -= fluidsReceived.get(stack).getAmount();
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
            for (StackListEntry<ItemStack> stack : itemsToReceive.getStacks()) {
                if (itemsReceived.get(stack.getStack()) != null) {
                    if (temp > itemsReceived.get(stack.getStack()).getCount() / itemsToReceive.get(stack.getStack()).getCount()) {
                        temp = itemsReceived.get(stack.getStack()).getCount() / itemsToReceive.get(stack.getStack()).getCount();
                    }
                } else {
                    temp = 0;
                }
            }
        }
        if (!fluidsToReceive.isEmpty()) {
            for (StackListEntry<FluidStack> stack : fluidsToReceive.getStacks()) {
                if (fluidsReceived.get(stack.getStack()) != null) {
                    if (temp > fluidsReceived.get(stack.getStack()).getAmount() / fluidsToReceive.get(stack.getStack()).getAmount()) {
                        temp = fluidsReceived.get(stack.getStack()).getAmount() / fluidsToReceive.get(stack.getStack()).getAmount();
                    }
                } else {
                    temp = 0;
                }
            }
        }
        finished = temp;
    }

    @Override
    public void reduceQuantity(){
        inserted++;
        super.reduceQuantity();
    }

    IStackList<ItemStack> getItemsToReceiveTotal() {
        return itemsToReceiveTotal;
    }

    IStackList<FluidStack> getFluidsToReceiveTotal() {
        return fluidsToReceiveTotal;
    }

    int getFluidReceivedCount(FluidStack stack) {
        if (fluidsReceived.get(stack) != null) {
            return fluidsReceived.get(stack).getAmount();
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
    public CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();

        tag.put(NBT_ITEMS_TO_RECEIVE, CraftingTask.writeItemStackList(itemsToReceive));
        tag.put(NBT_FLUIDS_TO_RECEIVE, CraftingTask.writeFluidStackList(fluidsToReceive));
        tag.put(NBT_FLUIDS_TO_PUT, CraftingTask.writeFluidStackList(fluidsToPut));
        tag.putInt(NBT_STATE, state.ordinal());
        tag.putInt(NBT_QUANTITY_TOTAL, totalQuantity);
        tag.put(NBT_ITEMS_RECEIVED, CraftingTask.writeItemStackList(itemsReceived));
        tag.put(NBT_FLUIDS_RECEIVED, CraftingTask.writeFluidStackList(fluidsReceived));
        tag.putInt(NBT_INSERTED, inserted);
        tag.putBoolean(NBT_HAS_DUPLICATES, hasDuplicates);

        return tag;
    }

    void enableDupeSets() {
        hasDuplicates = true;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}
