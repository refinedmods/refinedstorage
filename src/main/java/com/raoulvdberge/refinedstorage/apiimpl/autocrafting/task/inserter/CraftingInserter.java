package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter;

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
        items.addLast(new CraftingInserterItem(stack, CraftingInserterItemStatus.WAITING));

        network.getCraftingManager().onTaskChanged();
    }

    public void insertOne() {
        CraftingInserterItem item = items.peekFirst();

        if (item != null) {
            CraftingInserterItemStatus currentStatus = item.getStatus();

            if (network.insertItem(item.getStack(), item.getStack().getCount(), true) == null) {
                ItemStack inserted = network.insertItem(item.getStack(), item.getStack().getCount(), false);
                if (inserted != null) {
                    throw new IllegalStateException("Could not insert item");
                }

                items.pop();

                network.getCraftingManager().onTaskChanged();
            } else if (currentStatus != CraftingInserterItemStatus.FULL) {
                item.setStatus(CraftingInserterItemStatus.FULL);

                network.getCraftingManager().onTaskChanged();
            }
        }
    }

    public void insertAll() {
        while (!items.isEmpty()) {
            CraftingInserterItem item = items.pop();

            network.insertItem(item.getStack(), item.getStack().getCount(), false);
        }

        network.getCraftingManager().onTaskChanged();
    }

    public Collection<CraftingInserterItem> getItems() {
        return items;
    }
}
