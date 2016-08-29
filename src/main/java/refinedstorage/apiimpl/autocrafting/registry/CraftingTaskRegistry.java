package refinedstorage.apiimpl.autocrafting.registry;

import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.registry.ICraftingTaskRegistry;

import java.util.HashMap;
import java.util.Map;

public class CraftingTaskRegistry implements ICraftingTaskRegistry {
    private Map<String, ICraftingTaskFactory> registry = new HashMap<>();

    @Override
    public void addFactory(String id, ICraftingTaskFactory factory) {
        registry.put(id, factory);
    }

    @Override
    public ICraftingTaskFactory getFactory(String id) {
        return registry.get(id);
    }
}
