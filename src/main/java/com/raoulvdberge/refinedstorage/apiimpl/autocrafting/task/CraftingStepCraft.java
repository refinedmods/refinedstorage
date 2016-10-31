package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Deque;

public class CraftingStepCraft extends CraftingStep {
    public static final String ID = "craft";

    public CraftingStepCraft(INetworkMaster network, ICraftingPattern pattern) {
        super(network, pattern);
    }

    public CraftingStepCraft(INetworkMaster network) {
        super(network);
    }

    @Override
    public boolean canStartProcessing(IItemStackList items, IFluidStackList fluids) {
        int compare = IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0);
        for (ItemStack stack : getToInsert()) {
            ItemStack actualStack = items.get(stack, compare);

            if (actualStack == null || actualStack.stackSize == 0 || !items.trackedRemove(actualStack, stack.stackSize, true)) {
                FluidStack fluidInItem = RSUtils.getFluidFromStack(stack, true);

                if (fluidInItem != null && RSUtils.hasFluidBucket(fluidInItem)) {
                    FluidStack fluidStack = fluids.get(fluidInItem, compare);
                    ItemStack bucket = items.get(RSUtils.EMPTY_BUCKET, compare);
                    if (bucket != null && fluidStack != null && fluids.trackedRemove(fluidStack, fluidInItem.amount, true) && items.trackedRemove(bucket, 1, true)) {
                        continue;
                    }
                }
                items.undo();
                fluids.undo();
                return false;
            }
        }
        items.undo();
        fluids.undo();
        return true;
    }

    @Override
    public void execute(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
        IItemStackList actualInputs = API.instance().createItemStackList();
        int compare = CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0);
        for (ItemStack insertStack : getToInsert()) {
            FluidStack fluidInItem = RSUtils.getFluidFromStack(insertStack, true);
            if (fluidInItem != null) {
                network.extractFluid(fluidInItem, fluidInItem.amount, compare);
                network.extractItem(RSUtils.EMPTY_BUCKET, 1, compare);
                actualInputs.add(insertStack.copy());
            } else {
                ItemStack input = network.extractItem(insertStack, insertStack.stackSize, compare);
                if (input != null) {
                    actualInputs.add(input);
                } else {
                    // Abort task re-insert taken stacks and reset state
                    toInsertItems.addAll(actualInputs.getStacks());
                    startedProcessing = false;
                    return;
                }
            }
        }

        ItemStack[] took = new ItemStack[9];
        for (int i = 0; i < pattern.getInputs().size(); i++) {
            ItemStack input = pattern.getInputs().get(i);
            if (input != null) {
                ItemStack actualInput = actualInputs.get(input, compare);
                ItemStack taken = ItemHandlerHelper.copyStackWithSize(actualInput, input.stackSize);
                took[i] = taken;
                actualInputs.remove(taken, true);
            }
        }

        for (ItemStack byproduct : (pattern.isOredict() ? pattern.getByproducts(took) : pattern.getByproducts())) {
            if (byproduct != null) {
                toInsertItems.add(byproduct.copy());
            }
        }

        for (ItemStack output : (pattern.isOredict() ? pattern.getOutputs(took) : pattern.getOutputs())) {
            if (output != null) {
                toInsertItems.add(output.copy());
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);
        return super.writeToNBT(tag);
    }
}
