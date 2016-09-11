package refinedstorage.api;

import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import refinedstorage.api.solderer.ISoldererRegistry;

import javax.annotation.Nonnull;

/**
 * Represents a Refined Storage API implementation.
 */
public interface IAPI {
    /**
     * @return the solderer registry
     */
    @Nonnull
    ISoldererRegistry getSoldererRegistry();

    /**
     * @return the crafting task registry
     */
    @Nonnull
    ICraftingTaskRegistry getCraftingTaskRegistry();
}
