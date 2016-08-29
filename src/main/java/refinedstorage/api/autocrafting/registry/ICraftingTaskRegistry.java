package refinedstorage.api.autocrafting.registry;

public interface ICraftingTaskRegistry {
    void addFactory(String id, ICraftingTaskFactory factory);

    ICraftingTaskFactory getFactory(String id);
}
