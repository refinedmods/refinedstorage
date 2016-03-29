package refinedstorage.storage;

import net.minecraft.inventory.IInventory;
import refinedstorage.tile.settings.ICompareSetting;
import refinedstorage.tile.settings.IModeSetting;
import refinedstorage.tile.settings.IRedstoneModeSetting;

public interface IStorageGui {
    String getName();

    int getPriority();

    void onPriorityChanged(int priority);

    IInventory getInventory();

    IRedstoneModeSetting getRedstoneModeSetting();

    ICompareSetting getCompareSetting();

    IModeSetting getModeSetting();

    int getStored();

    int getCapacity();
}
