package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Deque;

public class CraftingStepProcess extends CraftingStep {
    public static final String ID = "process";

    public CraftingStepProcess(INetworkMaster network, ICraftingPattern pattern) {
        super(network, pattern);
    }

    public CraftingStepProcess(INetworkMaster network) {
        super(network);
    }

    @Override
    public boolean canStartProcessing(IItemStackList items, IFluidStackList fluids) {
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        if (inventory != null) {
            for (ItemStack stack : getToInsert()) {
                ItemStack actualStack = items.get(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));

                boolean canInsert = ItemHandlerHelper.insertItem(inventory, ItemHandlerHelper.copyStackWithSize(actualStack, stack.stackSize), true) == null;
                if (actualStack == null || actualStack.stackSize == 0 || !items.trackedRemove(actualStack, stack.stackSize, true) || !canInsert) {
                    items.undo();
                    return false;
                }
            }
            items.undo();
            return true;
        }
        return false;
    }

    @Override
    public boolean canStartProcessing() {
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        for (ItemStack stack : getToInsert()) {
            if (ItemHandlerHelper.insertItem(inventory, stack, true) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void execute(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
        // @TODO: fluid handling
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        int compare = CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0);
        for (ItemStack insertStack : getToInsert()) {
            ItemStack tookStack = network.extractItem(insertStack, insertStack.stackSize, compare);
            ItemHandlerHelper.insertItem(inventory, tookStack, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);

        return super.writeToNBT(tag);
    }
}
