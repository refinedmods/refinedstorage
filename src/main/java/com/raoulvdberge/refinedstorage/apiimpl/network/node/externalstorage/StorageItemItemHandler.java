package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class StorageItemItemHandler extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private Supplier<IItemHandler> handlerSupplier;
    private AccessType lockedAccessType = AccessType.INSERT_EXTRACT;

    public StorageItemItemHandler(NetworkNodeExternalStorage externalStorage, Supplier<IItemHandler> handlerSupplier) {
        this.externalStorage = externalStorage;
        this.handlerSupplier = handlerSupplier;

        if (externalStorage.getFacingTile().getBlockType().getUnlocalizedName().equals("tile.ExtraUtils2:TrashCan")) {
            lockedAccessType = AccessType.INSERT;
        }
    }

    @Override
    public int getCapacity() {
        IItemHandler handler = handlerSupplier.get();

        return handler != null ? handler.getSlots() * 64 : 0;
    }

    @Override
    public NonNullList<ItemStack> getStacks() {
        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return RSUtils.emptyNonNullList();
        }

        NonNullList<ItemStack> stacks = NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);

        for (int i = 0; i < handler.getSlots(); ++i) {
            stacks.set(i, handler.getStackInSlot(i).copy());
        }

        return stacks;
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        IItemHandler handler = handlerSupplier.get();

        if (handler != null && IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            return ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        int remaining = size;

        ItemStack received = null;

        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return null;
        }

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack slot = handler.getStackInSlot(i);

            if (!slot.isEmpty() && API.instance().getComparer().isEqual(slot, stack, flags)) {
                ItemStack got = handler.extractItem(i, remaining, simulate);

                if (!got.isEmpty()) {
                    if (received == null) {
                        received = got;
                    } else {
                        received.grow(got.getCount());
                    }

                    remaining -= got.getCount();

                    if (remaining == 0) {
                        break;
                    }
                }
            }
        }

        return received;
    }

    @Override
    public int getStored() {
        IItemHandler handler = handlerSupplier.get();

        if (handler == null) {
            return 0;
        }

        int size = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                size += handler.getStackInSlot(i).getCount();
            }
        }

        return size;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }

    @Override
    public AccessType getAccessType() {
        return ((lockedAccessType != AccessType.INSERT_EXTRACT) ? lockedAccessType : externalStorage.getAccessType());
    }
}
