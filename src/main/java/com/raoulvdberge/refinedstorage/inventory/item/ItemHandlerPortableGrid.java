package com.raoulvdberge.refinedstorage.inventory.item;

import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemHandlerPortableGrid implements IItemHandler {
    private TilePortableGrid portableGrid;

    public ItemHandlerPortableGrid(TilePortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public int getSlots() {
        if (portableGrid.getStorageCache() != null) {
            // One additional slot for possible input.
            return portableGrid.getStorageCache().getList().getStacks().size() + 1;
        }

        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (portableGrid.getStorageCache() != null) {
            List<ItemStack> stacks = portableGrid.getStorageCache().getList().getStacks();

            if (slot < stacks.size()) {
                return stacks.get(slot);
            }
        }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (portableGrid.getStorage() != null) {
            return StackUtils.nullToEmpty(portableGrid.getStorage().insert(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM));
        }

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (portableGrid.getStorage() != null) {
            ItemStack stack = getStackInSlot(slot);

            return StackUtils.nullToEmpty(portableGrid.getStorage().extract(stack, stack.getCount(), IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE, simulate ? Action.SIMULATE : Action.PERFORM));
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }
}
