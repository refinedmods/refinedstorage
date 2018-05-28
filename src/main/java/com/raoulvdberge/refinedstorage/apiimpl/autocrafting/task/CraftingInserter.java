package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class CraftingInserter {
    private INetwork network;
    private Deque<CraftingInserterItem> items = new ArrayDeque<>();

    public CraftingInserter(INetwork network) {
        this.network = network;
    }

    public void insert(ItemStack stack) {
        items.push(new CraftingInserterItem(stack, CraftingInserterItemStatus.WAITING));

        network.getCraftingManager().sendCraftingMonitorUpdate();
    }

    public void insertSingle() {
        CraftingInserterItem item = items.peek();

        if (item != null) {
            if (network.insertItem(item.getStack(), item.getStack().getCount(), true) == null) {
                ItemStack inserted = network.insertItem(item.getStack(), item.getStack().getCount(), false);
                if (inserted != null) {
                    throw new IllegalStateException("Could not insert item");
                }

                items.pop();
            } else {
                item.setStatus(CraftingInserterItemStatus.FULL);
            }

            network.getCraftingManager().sendCraftingMonitorUpdate();
        }
    }

    public Collection<CraftingInserterItem> getItems() {
        return items;
    }
}
