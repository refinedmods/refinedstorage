package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class CraftingInserter {
    private static final String NBT_ITEM = "Item";
    private static final String NBT_STATUS = "Status";

    private INetwork network;
    private Deque<CraftingInserterItem> items = new ArrayDeque<>();

    public CraftingInserter(INetwork network) {
        this.network = network;
    }

    public CraftingInserter(INetwork network, NBTTagList list) throws CraftingTaskReadException {
        this(network);

        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);

            ItemStack stack = StackUtils.deserializeStackFromNbt(itemTag.getCompoundTag(NBT_ITEM));

            if (stack.isEmpty()) {
                throw new CraftingTaskReadException("Inserter has empty stack");
            }

            CraftingInserterItemStatus status = CraftingInserterItemStatus.values()[itemTag.getInteger(NBT_STATUS)];

            items.push(new CraftingInserterItem(stack, status));
        }
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

    public NBTTagList writeToNbt() {
        NBTTagList list = new NBTTagList();

        for (CraftingInserterItem item : items) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setTag(NBT_ITEM, StackUtils.serializeStackToNbt(item.getStack()));
            tag.setInteger(NBT_STATUS, item.getStatus().ordinal());

            list.appendTag(tag);
        }

        return list;
    }
}
