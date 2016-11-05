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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            Deque<ItemStack> toInsert = new LinkedList<>();
            for (ItemStack stack : getToInsert()) {
                ItemStack actualStack = items.get(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0));
                ItemStack removeStack = ItemHandlerHelper.copyStackWithSize(actualStack, stack.stackSize);
                if (actualStack == null || actualStack.stackSize == 0 || !items.trackedRemove(removeStack, true)) {
                    items.undo();
                    return false;
                }
                toInsert.add(removeStack.copy());
            }
            items.undo();
            return insertSimulation(inventory, toInsert);
        }
        return false;
    }

    @Override
    public boolean canStartProcessing() {
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        return inventory != null && insertSimulation(inventory, new LinkedList<>(getToInsert()));
    }

    @Override
    public void execute(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
        // @TODO: fluid handling
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        int compare = CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0);
        for (ItemStack insertStack : getToInsert()) {
            ItemStack tookStack = network.extractItem(insertStack, insertStack.stackSize, compare, false);
            ItemHandlerHelper.insertItem(inventory, tookStack, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);

        return super.writeToNBT(tag);
    }

    /**
     * Checks whether all stacks can be inserted or not
     *
     * @param dest   target {@link IItemHandler}
     * @param stacks a {@link Deque} of {@link ItemStack}s
     * @return true when all can be inserted, false otherwise
     */
    private static boolean insertSimulation(IItemHandler dest, Deque<ItemStack> stacks) {
        ItemStack current = stacks.poll();
        List<Integer> availableSlots = IntStream.range(0, dest.getSlots()).boxed().collect(Collectors.toList());
        while (current != null && !availableSlots.isEmpty()) {
            ItemStack remainder = null;
            for (Integer slot : availableSlots) {
                remainder = dest.insertItem(slot, current, true);
                if (remainder == null || current.stackSize != remainder.stackSize) {
                    availableSlots.remove(slot);
                    break;
                }
            }
            if (remainder == null || remainder.stackSize <= 0) {
                current = stacks.poll();
            } else if (current.stackSize == remainder.stackSize) {
                break; // Can't be inserted
            } else {
                current = remainder;
            }
        }
        return current == null && stacks.isEmpty();
    }
}
