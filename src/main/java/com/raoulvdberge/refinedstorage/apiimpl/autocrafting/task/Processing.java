package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

class Processing {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ITEMS_TO_RECEIVE = "ItemsToReceive";
    private static final String NBT_FLUIDS_TO_RECEIVE = "FluidsToReceive";
    private static final String NBT_ITEMS_TO_PUT = "ItemsToPut";
    private static final String NBT_FLUIDS_TO_PUT = "FluidsToPut";
    private static final String NBT_STATE = "State";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_QUANTITY_TOTAL = "TotalQuantity";
    private static final String NBT_ITEMS_RECEIVED = "ItemsReceived";
    private static final String NBT_FLUIDS_RECEIVED = "FluidsReceived";
    private static final String NBT_CONTAINERS = "Containers";

    private ICraftingPattern pattern;

    private IStackList<ItemStack> itemsToReceive;
    private IStackList<FluidStack> fluidsToReceive;
    private IStackList<ItemStack> itemsToPut;
    private IStackList<FluidStack> fluidsToPut;
    private IStackList<ItemStack> itemsReceived;
    private IStackList<FluidStack> fluidsReceived;
    private IStackList<ItemStack> itemsToReceiveTotal;
    private IStackList<FluidStack> fluidsToReceiveTotal;
    private IStackList<ItemStack> itemsToPutTotal;
    private IStackList<FluidStack> fluidsToPutTotal;

    private ProcessingState state = ProcessingState.READY_OR_PROCESSING;
    private boolean root;
    private int quantity;
    private int totalQuantity;
    private int finished;
    private List<ICraftingPatternContainer> containers = new ArrayList<>();

    public Processing(ICraftingPattern pattern, int quantity, IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, IStackList<ItemStack> itemsToPut, IStackList<FluidStack> fluidsToPut, boolean root) {
        this.pattern = pattern;
        this.quantity = quantity;
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.itemsToPut = itemsToPut;
        this.fluidsToPut = fluidsToPut;
        this.root = root;
        itemsReceived = API.instance().createItemStackList();
        fluidsReceived = API.instance().createFluidStackList();
        containers.add(pattern.getContainer());
    }

    public Processing(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.itemsToReceive = CraftingTask.readItemStackList(tag.getTagList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.root = tag.getBoolean(NBT_ROOT);
        this.itemsToPut = CraftingTask.readItemStackList(tag.getTagList(NBT_ITEMS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.fluidsToPut = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_TO_PUT, Constants.NBT.TAG_COMPOUND));
        this.state = ProcessingState.values()[tag.getInteger(NBT_STATE)];
        this.quantity = tag.getInteger(NBT_QUANTITY);
        this.totalQuantity = tag.getInteger(NBT_QUANTITY_TOTAL);
        this.itemsReceived = CraftingTask.readItemStackList(tag.getTagList(NBT_ITEMS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.fluidsReceived = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.containers = CraftingTask.readContainerList(tag.getTagList(NBT_CONTAINERS,Constants.NBT.TAG_COMPOUND),network.world());
        setTotals(false);
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public void setTotals(boolean setNewTotal) {
        if (setNewTotal) {
            totalQuantity = quantity;
        }
        itemsToReceiveTotal = itemsToReceive.copy();
        itemsToReceiveTotal.getStacks().forEach(x -> x.setCount(x.getCount() * totalQuantity));
        fluidsToReceiveTotal = fluidsToReceive.copy();
        fluidsToReceiveTotal.getStacks().forEach(x -> x.amount *= totalQuantity);
        itemsToPutTotal = itemsToPut.copy();
        itemsToPutTotal.getStacks().forEach(x -> x.setCount(x.getCount() * totalQuantity));
        fluidsToPutTotal = fluidsToPut.copy();
        fluidsToPutTotal.getStacks().forEach(x -> x.amount *= totalQuantity);
    }

    public void addQuantity() {
        quantity++;
    }

    public void reduceQuantity() {
        quantity--;
    }

    public boolean nothingIsProcessing() {
        return (totalQuantity-quantity) == finished; // no items are in processing
    }

    public int getProcessing(ItemStack stack){ // insertedItems - finishedItems
        if (itemsToPut.get(stack) != null) {
            return  (itemsToPut.get(stack).getCount()*(totalQuantity-quantity)) -
                    (itemsToPut.get(stack).getCount()*finished);
        }
        return 0;
    }

    /* Calculates how many patterns were already finished
       by calculating finished patterns for every output
       and then taking the minimum of those
    */
    private void updateFinishedPatterns() {
        if(!itemsReceived.isEmpty()){
            int temp = totalQuantity;
            for(ItemStack stack : itemsReceived.getStacks()) {
                if(temp > Math.floorDiv(stack.getCount(),itemsToReceive.get(stack).getCount())) {
                    temp = Math.floorDiv(stack.getCount(),itemsToReceive.get(stack).getCount());
                }
            }
            for(FluidStack stack :fluidsReceived.getStacks()) {
                if(temp > Math.floorDiv(stack.amount,fluidsToReceive.get(stack).amount)) {
                    temp = Math.floorDiv(stack.amount,fluidsToReceive.get(stack).amount);
                }
            }
            finished = temp;
        }
    }

    public int getProcessing(FluidStack stack){
        if (fluidsToPut.get(stack) != null) {
            return  fluidsToPut.get(stack).amount*(totalQuantity-quantity) -
                    (fluidsToPut.get(stack).amount*finished);
        }
        return 0;
    }

    public int getScheduled(ItemStack stack) {
        if (itemsToReceiveTotal.get(stack) != null) {
            int scheduled = itemsToReceiveTotal.get(stack).getCount();
            if (itemsReceived.get(stack) != null) {
                scheduled -=itemsReceived.get(stack).getCount();
            }
            return scheduled;
        }
        return 0;
    }

    public int getScheduled(FluidStack stack) {
        if (fluidsToReceiveTotal.get(stack) != null) {
            int scheduled = fluidsToReceiveTotal.get(stack).amount;
            if (fluidsReceived.get(stack) != null) {
                scheduled -= fluidsReceived.get(stack).amount;
            }
            return scheduled;
        }
        return 0;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean calculateFinished(ItemStack received, int size) {
        itemsReceived.add(received, size);
        return isFinished();
    }

    private boolean isFinished() {
         updateFinishedPatterns();
        return finished== totalQuantity;
    }

    public boolean calculateFinished(FluidStack recieved, int size) {
        fluidsReceived.add(recieved, size);
        return isFinished();
    }

    public IStackList<ItemStack> getItemsToPut() {
        return itemsToPut;
    }

    public IStackList<FluidStack> getFluidsToPut() {
        return fluidsToPut;
    }

    public IStackList<ItemStack> getItemsToReceiveTotal() {
        return itemsToReceiveTotal;
    }

    public IStackList<FluidStack> getFluidsToReceiveTotal() {
        return fluidsToReceiveTotal;
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

    public void addContainer(ICraftingPatternContainer container) {
        containers.add(container);
    }
    public List<ICraftingPatternContainer> getContainer() {
        return containers;
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.setTag(NBT_ITEMS_TO_RECEIVE, CraftingTask.writeItemStackList(itemsToReceive));
        tag.setTag(NBT_FLUIDS_TO_RECEIVE, CraftingTask.writeFluidStackList(fluidsToReceive));
        tag.setBoolean(NBT_ROOT, root);
        tag.setTag(NBT_ITEMS_TO_PUT, CraftingTask.writeItemStackList(itemsToPut));
        tag.setTag(NBT_FLUIDS_TO_PUT, CraftingTask.writeFluidStackList(fluidsToPut));
        tag.setInteger(NBT_STATE, state.ordinal());
        tag.setInteger(NBT_QUANTITY, quantity);
        tag.setInteger(NBT_QUANTITY_TOTAL, totalQuantity);
        tag.setTag(NBT_ITEMS_RECEIVED, CraftingTask.writeItemStackList(itemsReceived));
        tag.setTag(NBT_FLUIDS_RECEIVED, CraftingTask.writeFluidStackList(fluidsReceived));
        tag.setTag(NBT_CONTAINERS,CraftingTask.writeContainerList(containers));
        return tag;
    }
}
