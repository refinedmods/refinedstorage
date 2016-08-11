package refinedstorage.tile.externalstorage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.tile.config.IFilterable;

import java.util.ArrayList;
import java.util.List;

public class ItemStorageItemHandler extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private IItemHandler handler;

    public ItemStorageItemHandler(TileExternalStorage externalStorage, IItemHandler handler) {
        this.externalStorage = externalStorage;
        this.handler = handler;
    }

    @Override
    public int getCapacity() {
        return handler.getSlots() * 64;
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null && handler.getStackInSlot(i).getItem() != null) {
                items.add(handler.getStackInSlot(i).copy());
            }
        }

        return items;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        if (IFilterable.canTake(externalStorage.getFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            return ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags) {
        int remaining = size;

        ItemStack received = null;

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack slot = handler.getStackInSlot(i);

            if (slot != null && CompareUtils.compareStack(slot, stack, flags)) {
                ItemStack got = handler.extractItem(i, remaining, false);

                if (got != null) {
                    if (received == null) {
                        received = got;
                    } else {
                        received.stackSize += got.stackSize;
                    }

                    remaining -= got.stackSize;

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
        int size = 0;

        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null) {
                size += handler.getStackInSlot(i).stackSize;
            }
        }

        return size;
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }
}
