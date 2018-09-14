package com.raoulvdberge.refinedstorage.container.transfer;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TransferManager {
    private Map<IInventoryWrapper, List<IInventoryWrapper>> fromToMap = new HashMap<>();
    private Container container;

    @Nullable
    private Function<Integer, ItemStack> notFoundHandler;

    public TransferManager(Container container) {
        this.container = container;
    }

    public void clearTransfers() {
        this.fromToMap.clear();
    }

    public void setNotFoundHandler(@Nullable Function<Integer, ItemStack> handler) {
        this.notFoundHandler = handler;
    }

    public void addTransfer(IInventory from, IItemHandler to) {
        addTransfer(new InventoryWrapperInventory(from), new InventoryWrapperItemHandler(to));
    }

    public void addTransfer(IInventory from, IInventory to) {
        addTransfer(new InventoryWrapperInventory(from), new InventoryWrapperInventory(to));
    }

    public void addFilterTransfer(IInventory from, IItemHandlerModifiable itemTo, FluidInventory fluidTo, Supplier<Integer> typeGetter) {
        addTransfer(new InventoryWrapperInventory(from), new InventoryWrapperFilter(itemTo, fluidTo, typeGetter));
    }

    public void addItemFilterTransfer(IInventory from, IItemHandlerModifiable to) {
        addTransfer(new InventoryWrapperInventory(from), new InventoryWrapperFilterItem(to));
    }

    public void addFluidFilterTransfer(IInventory from, FluidInventory to) {
        addTransfer(new InventoryWrapperInventory(from), new InventoryWrapperFilterFluid(to));
    }

    public void addTransfer(IItemHandler from, IInventory to) {
        addTransfer(new InventoryWrapperItemHandler(from), new InventoryWrapperInventory(to));
    }

    public void addBiTransfer(IInventory from, IItemHandler to) {
        addTransfer(from, to);
        addTransfer(to, from);
    }

    private void addTransfer(IInventoryWrapper from, IInventoryWrapper to) {
        List<IInventoryWrapper> toList = fromToMap.computeIfAbsent(from, k -> new LinkedList<>());

        toList.add(to);
    }

    public ItemStack transfer(int index) {
        Slot slot = container.getSlot(index);

        IInventoryWrapper key;
        if (slot instanceof SlotItemHandler) {
            key = new InventoryWrapperItemHandler(((SlotItemHandler) slot).getItemHandler());
        } else {
            key = new InventoryWrapperInventory(slot.inventory);
        }

        List<IInventoryWrapper> toList = fromToMap.get(key);

        if (toList != null) {
            ItemStack initial = slot.getStack().copy();

            ItemStack remainder = slot.getStack();

            for (IInventoryWrapper to : toList) {
                InsertionResult result = to.insert(remainder);

                if (result.getType() == InsertionResultType.STOP) {
                    break;
                } else if (result.getType() == InsertionResultType.CONTINUE_IF_POSSIBLE) {
                    remainder = result.getValue();

                    if (remainder.isEmpty()) {
                        break;
                    }
                }
            }

            slot.putStack(remainder);
            slot.onSlotChanged();

            if (API.instance().getComparer().isEqual(remainder, initial) && notFoundHandler != null) {
                return notFoundHandler.apply(index);
            }
        } else if (notFoundHandler != null) {
            return notFoundHandler.apply(index);
        }

        return ItemStack.EMPTY;
    }
}
