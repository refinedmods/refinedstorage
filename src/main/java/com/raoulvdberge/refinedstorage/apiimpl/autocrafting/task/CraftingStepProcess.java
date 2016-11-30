package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IFluidStackList;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;
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
                    toInsert.add(ItemHandlerHelper.copyStackWithSize(actualStack, stack.stackSize));
                } else if (type == AvailableType.FLUID) {
                    toInsert.add(ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize));
                } else {
                    items.undo();
                    fluids.undo();
                    return false;
                }
            }

            items.undo();
            fluids.undo();
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
        List<ItemStack> actualInputs = new LinkedList<>();
        int compare = CraftingTask.DEFAULT_COMPARE | (getPattern().isOredict() ? IComparer.COMPARE_OREDICT : 0);
        if (extractItems(actualInputs, compare, toInsertItems)) {
            IItemHandler inventory = getPattern().getContainer().getFacingInventory();
            if (insertSimulation(inventory, new ArrayDeque<>(actualInputs))) {
                actualInputs.forEach(stack -> ItemHandlerHelper.insertItem(inventory, stack, false));
            }
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
