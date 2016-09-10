package refinedstorage.api;

import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import refinedstorage.api.solderer.ISoldererRegistry;

public interface IAPI {
    ISoldererRegistry getSoldererRegistry();

    ICraftingTaskRegistry getCraftingTaskRegistry();
}
