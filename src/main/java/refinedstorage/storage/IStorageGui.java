package refinedstorage.storage;

import net.minecraftforge.items.IItemHandler;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.tile.config.IModeConfig;
import refinedstorage.tile.config.IRedstoneModeConfig;

public interface IStorageGui {
    String getGuiTitle();

    int getPriority();

    void onPriorityChanged(int priority);

    IItemHandler getFilters();

    IRedstoneModeConfig getRedstoneModeConfig();

    ICompareConfig getCompareConfig();

    IModeConfig getModeConfig();

    int getStored();

    int getCapacity();
}
