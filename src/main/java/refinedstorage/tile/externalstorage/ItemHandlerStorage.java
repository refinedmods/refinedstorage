package refinedstorage.tile.externalstorage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.tile.config.ModeFilter;

import java.util.List;

public class ItemHandlerStorage extends ExternalStorage {
    private TileExternalStorage externalStorage;
    private IItemHandler handler;

    public ItemHandlerStorage(TileExternalStorage externalStorage, IItemHandler handler) {
        this.externalStorage = externalStorage;
        this.handler = handler;
    }

    @Override
    public int getCapacity() {
        return handler.getSlots() * 64;
    }

    @Override
    public void addItems(List<ItemStack> items) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            if (handler.getStackInSlot(i) != null && handler.getStackInSlot(i).getItem() != null) {
                items.add(handler.getStackInSlot(i).copy());
            }
        }
    }

    @Override
    public ItemStack push(ItemStack stack, int size, boolean simulate) {
        if (ModeFilter.respectsMode(externalStorage.getFilters(), externalStorage, externalStorage.getCompare(), stack)) {
            return ItemHandlerHelper.insertItem(handler, ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack take(ItemStack stack, int size, int flags) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack slot = handler.getStackInSlot(i);

            if (slot != null && RefinedStorageUtils.compareStack(slot, stack, flags)) {
                size = Math.min(size, slot.stackSize);

                ItemStack took = ItemHandlerHelper.copyStackWithSize(slot, size);

                handler.extractItem(i, size, false);

                return took;
            }
        }

        return null;
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
