package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CraftingStepProcess extends CraftingStep {
    public static final String ID = "process";

    public CraftingStepProcess(INetworkMaster network, ICraftingPattern pattern, List<ICraftingStep> preliminarySteps) {
        super(network, pattern, preliminarySteps);
    }

    public CraftingStepProcess(INetworkMaster network) {
        super(network);
    }

    @Override
    public boolean canStartProcessing(IStackList<ItemStack> items, IStackList<FluidStack> fluids) {
        if (!super.canStartProcessing()) {
            return false;
        }
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        int compare = CraftingTask.DEFAULT_COMPARE | (pattern.isOredict() ? IComparer.COMPARE_OREDICT : 0);
        if (inventory != null) {
            Deque<ItemStack> toInsert = new LinkedList<>();
            for (ItemStack stack : getToInsert()) {
                // This will be a tool, like a hammer
                if (stack.isItemStackDamageable()) {
                    compare &= ~IComparer.COMPARE_DAMAGE;
                } else {
                    compare |= IComparer.COMPARE_DAMAGE;
                }

                ItemStack actualStack = items.get(stack, compare);
                AvailableType type = isItemAvailable(items, fluids, stack, actualStack, compare);

                if (type == AvailableType.ITEM) {
                    toInsert.add(ItemHandlerHelper.copyStackWithSize(actualStack, stack.getCount()));
                } else if (type == AvailableType.FLUID) {
                    toInsert.add(ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()));
                } else {
                    items.undo();
                    fluids.undo();
                    return false;
                }
            }

            items.undo();
            fluids.undo();
            return insert(inventory, toInsert, true);
        }
        return false;
    }

    @Override
    public boolean canStartProcessing() {
        if (!super.canStartProcessing()) {
            return false;
        }
        IItemHandler inventory = getPattern().getContainer().getFacingInventory();
        return inventory != null && insert(inventory, new LinkedList<>(getToInsert()), true);
    }

    @Override
    public void execute(Deque<ItemStack> toInsertItems, Deque<FluidStack> toInsertFluids) {
        LinkedList<ItemStack> actualInputs = new LinkedList<>();
        int compare = CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0);
        if (extractItems(actualInputs, compare, toInsertItems)) {
            IItemHandler inventory = getPattern().getContainer().getFacingInventory();
            if (insert(inventory, new ArrayDeque<>(actualInputs), true)) {
                insert(inventory, actualInputs, false);
            } else {
                // Something went wrong here, redo!
                toInsertItems.addAll(actualInputs);
                startedProcessing = false;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_CRAFTING_STEP_TYPE, ID);

        return super.writeToNBT(tag);
    }

    /**
     * Insert or simulate insertion of {@link ItemStack}s into an {@link IItemHandler}
     *
     * @param dest     target {@link IItemHandler}
     * @param stacks   a {@link Deque} of {@link ItemStack}s
     * @param simulate simulate or actually insert the {@link ItemStack}s
     * @return true when all can be inserted, false otherwise
     */
    private static boolean insert(IItemHandler dest, Deque<ItemStack> stacks, boolean simulate) {
        ItemStack current = stacks.poll();
        List<Integer> availableSlots = IntStream.range(0, dest.getSlots()).boxed().collect(Collectors.toList());
        while (current != null && !availableSlots.isEmpty()) {
            ItemStack remainder = null;
            for (Integer slot : availableSlots) {
                remainder = dest.insertItem(slot, current, simulate);
                if (remainder.isEmpty() || current.getCount() != remainder.getCount()) {
                    availableSlots.remove(slot);
                    break;
                }
            }
            if (remainder == null || remainder.isEmpty()) {
                current = stacks.poll();
            } else if (current.getCount() == remainder.getCount()) {
                break; // Can't be inserted
            } else {
                current = remainder;
            }
        }
        return current == null && stacks.isEmpty();
    }
}
