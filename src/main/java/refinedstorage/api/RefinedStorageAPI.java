package refinedstorage.api;

import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;
import refinedstorage.api.solderer.ISoldererRegistry;

public final class RefinedStorageAPI {
    /**
     * The solderer registry, set in pre-initialization
     */
    public static ISoldererRegistry SOLDERER_REGISTRY;

    /**
     * The crafting task registry, set in pre-initialization
     */
    public static ICraftingTaskRegistry CRAFTING_TASK_REGISTRY;
}
