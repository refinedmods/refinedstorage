package refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.tile.config.ModeFilter;

import java.util.List;

public class DrawerStorage extends ExternalStorage {
    private TileExternalStorage externalStorage;
    private IDrawer drawer;

    public DrawerStorage(TileExternalStorage externalStorage, IDrawer drawer) {
        this.externalStorage = externalStorage;
        this.drawer = drawer;
    }

    @Override
    public int getCapacity() {
        return drawer.getMaxCapacity();
    }

    @Override
    public void addItems(List<ItemStack> items) {
        if (!drawer.isEmpty()) {
            items.add(drawer.getStoredItemCopy());
        }
    }

    @Override
    public ItemStack push(ItemStack stack, boolean simulate) {
        if (ModeFilter.respectsMode(externalStorage.getFilters(), externalStorage, externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
            if (drawer.isEmpty()) {
                if (getStored() + stack.stackSize > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return stack;
                    }

                    if (!simulate) {
                        drawer.setStoredItem(stack, remainingSpace);
                    }

                    return ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - remainingSpace);
                } else {
                    if (!simulate) {
                        drawer.setStoredItem(stack, stack.stackSize);
                    }

                    return null;
                }
            } else {
                if (getStored() + stack.stackSize > getCapacity()) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return stack;
                    }

                    if (!simulate) {
                        drawer.setStoredItemCount(drawer.getStoredItemCount() + remainingSpace);
                    }

                    return ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - remainingSpace);
                } else {
                    if (!simulate) {
                        drawer.setStoredItemCount(drawer.getStoredItemCount() + stack.stackSize);
                    }

                    return null;
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack take(ItemStack stack, int size, int flags) {
        return null;
    }

    @Override
    public int getStored() {
        return drawer.getStoredItemCount();
    }

    @Override
    public int getPriority() {
        return externalStorage.getPriority();
    }
}
