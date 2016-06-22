package refinedstorage.api.autocrafting;

import net.minecraftforge.items.IItemHandler;

public interface ICraftingPatternContainer {
    int getSpeed();

    IItemHandler getConnectedInventory();
}
