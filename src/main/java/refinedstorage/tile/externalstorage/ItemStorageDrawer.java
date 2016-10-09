package refinedstorage.tile.externalstorage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.apiimpl.API;
import refinedstorage.tile.config.IAccessType;
import refinedstorage.tile.config.IFilterable;

import java.util.Collections;
import java.util.List;

public class ItemStorageDrawer extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private IDrawer drawer;

    public ItemStorageDrawer(TileExternalStorage externalStorage, IDrawer drawer) {
        this.externalStorage = externalStorage;
        this.drawer = drawer;
    }

    @Override
    public int getCapacity() {
        return drawer.getMaxCapacity();
    }

    @Override
    public List<ItemStack> getItems() {
        if(externalStorage.getAccessType() == IAccessType.READ) {
            return Collections.emptyList();
        }

        if (!drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
            return Collections.singletonList(drawer.getStoredItemCopy());
        }

        return Collections.emptyList();
    }

    private boolean isVoidable() {
        return drawer instanceof IVoidable && ((IVoidable) drawer).isVoid();
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        if(externalStorage.getAccessType() == IAccessType.READ) {
            return stack;
        }

        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack) && drawer.canItemBeStored(stack)) {
            if (!drawer.isEmpty()) {
                if (getStored() + size > drawer.getMaxCapacity(stack)) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return isVoidable() ? null : ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        drawer.setStoredItemCount(drawer.getStoredItemCount() + remainingSpace);
                    }

                    return isVoidable() ? null : ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        drawer.setStoredItemCount(drawer.getStoredItemCount() + size);
                    }

                    return null;
                }
            } else {
                if (getStored() + size > drawer.getMaxCapacity(stack)) {
                    int remainingSpace = getCapacity() - getStored();

                    if (remainingSpace <= 0) {
                        return isVoidable() ? null : ItemHandlerHelper.copyStackWithSize(stack, size);
                    }

                    if (!simulate) {
                        drawer.setStoredItem(stack, remainingSpace);
                    }

                    return isVoidable() ? null : ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace);
                } else {
                    if (!simulate) {
                        drawer.setStoredItem(stack, size);
                    }

                    return null;
                }
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags) {
        if(externalStorage.getAccessType() == IAccessType.READ) {
            return null;
        }

        if (API.instance().getComparer().isEqual(stack, drawer.getStoredItemPrototype(), flags) && drawer.canItemBeExtracted(stack)) {
            if (size > drawer.getStoredItemCount()) {
                size = drawer.getStoredItemCount();
            }

            ItemStack stored = drawer.getStoredItemPrototype();

            drawer.setStoredItemCount(drawer.getStoredItemCount() - size);

            return ItemHandlerHelper.copyStackWithSize(stored, size);
        }

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
