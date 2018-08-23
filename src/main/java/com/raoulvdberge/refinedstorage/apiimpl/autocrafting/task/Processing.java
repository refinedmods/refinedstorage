package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

    private ICraftingPattern pattern;
    private IStackList<ItemStack> itemsToReceive;
    private IStackList<FluidStack> fluidsToReceive;
    private ArrayList<ItemStack> itemsToPut;
    private ArrayList<FluidStack> fluidsToPut;
    private ProcessingState state = ProcessingState.READY;
    private boolean root;

    public Processing(ICraftingPattern pattern, IStackList<ItemStack> itemsToReceive, IStackList<FluidStack> fluidsToReceive, ArrayList<ItemStack> itemsToPut, ArrayList<FluidStack> fluidsToPut, boolean root) {
        this.pattern = pattern;
        this.itemsToReceive = itemsToReceive;
        this.fluidsToReceive = fluidsToReceive;
        this.itemsToPut = itemsToPut;
        this.fluidsToPut = fluidsToPut;
        this.root = root;
    }

    public Processing(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.itemsToReceive = CraftingTask.readItemStackList(tag.getTagList(NBT_ITEMS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.fluidsToReceive = CraftingTask.readFluidStackList(tag.getTagList(NBT_FLUIDS_TO_RECEIVE, Constants.NBT.TAG_COMPOUND));
        this.root = tag.getBoolean(NBT_ROOT);

        this.itemsToPut = new ArrayList<>();

        NBTTagList itemsToPutList = tag.getTagList(NBT_ITEMS_TO_PUT, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < itemsToPutList.tagCount(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(itemsToPutList.getCompoundTagAt(i));

            if (stack.isEmpty()) {
                throw new CraftingTaskReadException("Stack is empty!");
            }

            itemsToPut.add(stack);
        }

        this.fluidsToPut = new ArrayList<>();

        NBTTagList fluidsToPutList = tag.getTagList(NBT_FLUIDS_TO_PUT, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < fluidsToPutList.tagCount(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidsToPutList.getCompoundTagAt(i));

            if (stack == null) {
                throw new CraftingTaskReadException("Stack is empty!");
            }

            fluidsToPut.add(stack);
        }

        this.state = ProcessingState.values()[tag.getInteger(NBT_STATE)];
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

    public boolean isRoot() {
        return root;
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.setTag(NBT_ITEMS_TO_RECEIVE, CraftingTask.writeItemStackList(itemsToReceive));
        tag.setTag(NBT_FLUIDS_TO_RECEIVE, CraftingTask.writeFluidStackList(fluidsToReceive));
        tag.setBoolean(NBT_ROOT, root);

        NBTTagList itemsToPutList = new NBTTagList();
        for (ItemStack stack : this.itemsToPut) {
            itemsToPutList.appendTag(StackUtils.serializeStackToNbt(stack));
        }

        tag.setTag(NBT_ITEMS_TO_PUT, itemsToPutList);

        NBTTagList fluidsToPutList = new NBTTagList();
        for (FluidStack stack : this.fluidsToPut) {
            fluidsToPutList.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_FLUIDS_TO_PUT, fluidsToPutList);

        tag.setInteger(NBT_STATE, state.ordinal());

        return tag;
    }
}
