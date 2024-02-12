package com.refinedmods.refinedstorage.container.transfer;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TransferManager {
    private final Map<IInventoryWrapper, List<IInventoryWrapper>> fromToMap = new HashMap<>();
    private final AbstractContainerMenu container;

    @Nullable
    private Function<Integer, ItemStack> notFoundHandler;

    public TransferManager(AbstractContainerMenu container) {
        this.container = container;
    }

    public void clearTransfers() {
        this.fromToMap.clear();
    }

    public void setNotFoundHandler(@Nullable Function<Integer, ItemStack> handler) {
        this.notFoundHandler = handler;
    }

    public void addTransfer(Container from, IItemHandler to) {
        addTransfer(new InventoryInventoryWrapper(from), new ItemHandlerInventoryWrapper(to));
    }

    public void addTransfer(Container from, Container to) {
        addTransfer(new InventoryInventoryWrapper(from), new InventoryInventoryWrapper(to));
    }

    public void addFilterTransfer(Container from, IItemHandlerModifiable itemTo, FluidInventory fluidTo, Supplier<Integer> typeGetter) {
        addTransfer(new InventoryInventoryWrapper(from), new FilterInventoryWrapper(itemTo, fluidTo, typeGetter));
    }

    public void addItemFilterTransfer(Container from, IItemHandlerModifiable to) {
        addTransfer(new InventoryInventoryWrapper(from), new ItemFilterInventoryWrapper(to));
    }

    public void addFluidFilterTransfer(Container from, FluidInventory to) {
        addTransfer(new InventoryInventoryWrapper(from), new FluidFilterInventoryWrapper(to));
    }

    public void addTransfer(IItemHandler from, Container to) {
        addTransfer(new ItemHandlerInventoryWrapper(from), new InventoryInventoryWrapper(to));
    }

    public void addBiTransfer(Container from, IItemHandler to) {
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
            key = new ItemHandlerInventoryWrapper(((SlotItemHandler) slot).getItemHandler());
        } else {
            key = new InventoryInventoryWrapper(slot.container);
        }

        List<IInventoryWrapper> toList = fromToMap.get(key);

        if (toList != null) {
            ItemStack initial = slot.getItem().copy();

            ItemStack remainder = slot.getItem();

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

            slot.set(remainder);
            slot.setChanged();

            if (API.instance().getComparer().isEqual(remainder, initial) && notFoundHandler != null) {
                return notFoundHandler.apply(index);
            }
        } else if (notFoundHandler != null) {
            return notFoundHandler.apply(index);
        }

        return ItemStack.EMPTY;
    }
}
