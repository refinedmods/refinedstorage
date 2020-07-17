package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.IoUtil;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.ProcessingState;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.SerializationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

public class ProcessingCraftingTaskNode extends CraftingTaskNode {
    private static final String NBT_ITEMS_TO_RECEIVE = "ItemsToReceive";
    private static final String NBT_FLUIDS_TO_RECEIVE = "FluidsToReceive";
    private static final String NBT_FLUIDS_TO_USE = "FluidsToUse";
    private static final String NBT_STATE = "State";
    private static final String NBT_QUANTITY_TOTAL = "TotalQuantity";
    private static final String NBT_ITEMS_RECEIVED = "ItemsReceived";
    private static final String NBT_FLUIDS_RECEIVED = "FluidsReceived";
    private static final String NBT_ITEMS_TO_DISPLAY = "ItemsToDisplay";

    private IStackList<ItemStack> itemsToReceive = API.instance().createItemStackList();
    private IStackList<ItemStack> itemsReceived = API.instance().createItemStackList();
    private IStackList<FluidStack> fluidsToReceive = API.instance().createFluidStackList();
    private IStackList<FluidStack> fluidsReceived = API.instance().createFluidStackList();
    private IStackList<FluidStack> fluidsToUse = API.instance().createFluidStackList();
    private IStackList<ItemStack> itemsToDisplay;
    private ProcessingState state = ProcessingState.READY;

    private int finished;
    private int totalQuantity;

    public ProcessingCraftingTaskNode(ICraftingPattern pattern, boolean root) {
        super(pattern, root);
    }

    public ProcessingCraftingTaskNode(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.itemsToReceive = SerializationUtil.readItemStackList(tag.getList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = SerializationUtil.readFluidStackList(tag.getList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.state = ProcessingState.values()[tag.getInt(NBT_STATE)];
        this.totalQuantity = tag.getInt(NBT_QUANTITY_TOTAL);
        this.itemsReceived = SerializationUtil.readItemStackList(tag.getList(NBT_ITEMS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.fluidsReceived = SerializationUtil.readFluidStackList(tag.getList(NBT_FLUIDS_RECEIVED, Constants.NBT.TAG_COMPOUND));
        this.fluidsToUse = SerializationUtil.readFluidStackList(tag.getList(NBT_FLUIDS_TO_USE, Constants.NBT.TAG_COMPOUND));
        this.itemsToDisplay = SerializationUtil.readItemStackList(tag.getList(NBT_ITEMS_TO_DISPLAY, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public void update(INetwork network, int ticks, CraftingTaskNodeList nodes, IStorageDisk<ItemStack> internalStorage, IStorageDisk<FluidStack> internalFluidStorage) {
        if (getState() == ProcessingState.PROCESSED) {
            nodes.remove(this);
            network.getCraftingManager().onTaskChanged();
            return;
        }

        //These are for handling multiple crafters with differing states
        boolean allLocked = true;
        boolean allNull = true;
        boolean allRejected = true;

        ProcessingState originalState = getState();

        for (ICraftingPatternContainer container : network.getCraftingManager().getAllContainer(getPattern())) {
            int interval = container.getUpdateInterval();

            if (interval < 0) {
                throw new IllegalStateException(container + " has an update interval of < 0");
            }

            if (interval == 0 || ticks % interval == 0) {
                for (int i = 0; i < container.getMaximumSuccessfulCraftingUpdates(); i++) {
                    if (getQuantity() <= 0) {
                        return;
                    }

                    if (container.isLocked()) {
                        if (allLocked) {
                            setState(ProcessingState.LOCKED);
                        }
                        break;
                    } else {
                        allLocked = false;
                    }
                    if ((hasItems() && container.getConnectedInventory() == null) ||
                        (hasFluids() && container.getConnectedFluidInventory() == null)) {
                        if (allNull) {
                            setState(ProcessingState.MACHINE_NONE);
                        }

                        break;
                    } else {
                        allNull = false;
                    }

                    boolean hasAll = false;
                    IStackList<ItemStack> extractedItems;
                    IStackList<FluidStack> extractedFluids = null;

                    extractedItems = IoUtil.extractFromInternalItemStorage(getItemsToUse(true).getStacks(), internalStorage, Action.SIMULATE);
                    if (extractedItems != null) {
                        extractedFluids = IoUtil.extractFromInternalFluidStorage(getFluidsToUse().getStacks(), internalFluidStorage, Action.SIMULATE);
                        if (extractedFluids != null) {
                            hasAll = true;
                        }
                    }

                    boolean canInsert = false;
                    if (hasAll) {
                        canInsert = IoUtil.insertIntoInventory(container.getConnectedInventory(), extractedItems.getStacks(), Action.SIMULATE);
                        if (canInsert) {
                            canInsert = IoUtil.insertIntoTank(container.getConnectedFluidInventory(), extractedFluids.getStacks(), Action.SIMULATE);
                        }
                    }

                    if (hasAll && !canInsert) {
                        if (allRejected) {
                            setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);
                        }
                        break;
                    } else {
                        allRejected = false;
                    }

                    if (hasAll && canInsert) {
                        setState(ProcessingState.READY);

                        IoUtil.extractFromInternalItemStorage(getItemsToUse(false).getStacks(), internalStorage, Action.PERFORM);
                        IoUtil.extractFromInternalFluidStorage(getFluidsToUse().getStacks(), internalFluidStorage, Action.PERFORM);

                        IoUtil.insertIntoInventory(container.getConnectedInventory(), extractedItems.getStacks(), Action.PERFORM);
                        IoUtil.insertIntoTank(container.getConnectedFluidInventory(), extractedFluids.getStacks(), Action.PERFORM);

                        next();
                        // TODO currentStep++;
                        network.getCraftingManager().onTaskChanged();
                        container.onUsedForProcessing();
                    }
                }

            }
        }

        if (originalState != getState()) {
            network.getCraftingManager().onTaskChanged();
        }
    }

    @Override
    public void finishCalculation() {
        this.totalQuantity = quantity;
        updateItemsToDisplay();
    }

    public int getNeeded(ItemStack stack) {
        if (itemsToReceive.get(stack) != null) {
            int needed = itemsToReceive.get(stack).getCount() * totalQuantity;
            if (itemsReceived.get(stack) != null) {
                needed -= itemsReceived.get(stack).getCount();
            }
            return needed;
        }
        return 0;
    }

    public int getNeeded(FluidStack stack) {
        if (fluidsToReceive.get(stack) != null) {
            int needed = fluidsToReceive.get(stack).getAmount() * totalQuantity;
            if (fluidsReceived.get(stack) != null) {
                needed -= fluidsReceived.get(stack).getAmount();
            }
            return needed;
        }
        return 0;
    }

    public boolean updateFinished() {
        int fin = finished;
        updateFinishedPatterns();
        if (finished == totalQuantity) {
            this.setState(ProcessingState.PROCESSED);
        }
        return fin != finished;
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

    public IStackList<ItemStack> getItemsToReceive() {
        return itemsToReceive;
    }

    public IStackList<FluidStack> getFluidsToReceive() {
        return fluidsToReceive;
    }

    public IStackList<ItemStack> getItemsToDisplay() {
        return itemsToDisplay;
    }

    private void updateItemsToDisplay() {
        itemsToDisplay = getItemsToUse(true);
    }

    public IStackList<FluidStack> getFluidsToUse() {
        return fluidsToUse;
    }

    public void addFluidsToUse(FluidStack stack) {
        fluidsToUse.add(stack);
    }

    public void addItemsToReceive(ItemStack stack) {
        itemsToReceive.add(stack);
    }

    public void addFluidsToReceive(FluidStack stack) {
        fluidsToReceive.add(stack);
    }

    public int getProcessing() {
        return totalQuantity - quantity - finished;
    }

    public void addFinished(ItemStack received, int size) {
        itemsReceived.add(received, size);
    }

    public void addFinished(FluidStack received, int size) {
        fluidsReceived.add(received, size);
    }

    public void setState(ProcessingState state) {
        this.state = state;
    }

    public ProcessingState getState() {
        return state;
    }

    public boolean hasFluids() {
        return !fluidsToUse.isEmpty();
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();
        tag.put(NBT_ITEMS_TO_RECEIVE, SerializationUtil.writeItemStackList(itemsToReceive));
        tag.put(NBT_FLUIDS_TO_RECEIVE, SerializationUtil.writeFluidStackList(fluidsToReceive));
        tag.putInt(NBT_STATE, state.ordinal());
        tag.putInt(NBT_QUANTITY_TOTAL, totalQuantity);
        tag.put(NBT_ITEMS_RECEIVED, SerializationUtil.writeItemStackList(itemsReceived));
        tag.put(NBT_FLUIDS_RECEIVED, SerializationUtil.writeFluidStackList(fluidsReceived));
        tag.put(NBT_FLUIDS_TO_USE, SerializationUtil.writeFluidStackList(fluidsToUse));
        tag.put(NBT_ITEMS_TO_DISPLAY, SerializationUtil.writeItemStackList(itemsToDisplay));

        return tag;
    }
}
