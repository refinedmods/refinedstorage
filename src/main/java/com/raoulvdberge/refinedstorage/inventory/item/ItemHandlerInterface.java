package com.raoulvdberge.refinedstorage.inventory.item;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemHandlerInterface implements IItemHandler {
    private INetworkNode node;

    public ItemHandlerInterface(INetworkNode node) {
        this.node = node;
    }

    @Nullable
    private INetwork getNetwork() {
        if (node.getNetwork() != null && node.canUpdate()) {
            return node.getNetwork();
        }

        return null;
    }

    @Override
    public int getSlots() {
        INetwork network = getNetwork();

        if (network != null) {
            // One additional slot for possible input.
            return network.getItemStorageCache().getList().getStacks().size() + 1;
        }

        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        INetwork network = getNetwork();

        if (network != null) {
            List<ItemStack> stacks = network.getItemStorageCache().getList().getStacks();

            if (slot < stacks.size()) {
                return stacks.get(slot);
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        INetwork network = getNetwork();

        if (network != null) {
            return StackUtils.nullToEmpty(network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM));
        }

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        INetwork network = getNetwork();

        if (network != null) {
            ItemStack stack = getStackInSlot(slot);

            return StackUtils.nullToEmpty(network.extractItem(stack, Math.min(amount, 64), simulate ? Action.SIMULATE : Action.PERFORM));
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }
}
