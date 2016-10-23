package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Deque;

public class ProcessCraftingStep extends AbstractCraftingStep {
    public static final String ID = "ProcessCraftingStep";

    public ProcessCraftingStep(INetworkMaster network, ICraftingPattern pattern) {
        super(network, pattern);
    }

    public ProcessCraftingStep(INetworkMaster network) {
        super(network);
    }

    @Override
    public void execute(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
        // TODO fluid handling
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        for (ItemStack insertStack : getToInsert()) {
            ItemStack tookStack = network.extractItem(insertStack, insertStack.stackSize, CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0));
            ItemHandlerHelper.insertItem(inventory, tookStack, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);
        return super.writeToNBT(tag);
    }
}
