package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Represents a crafting pattern.
 */
public interface ICraftingPattern {
    /**
     * @return The container where the pattern is in
     */
    ICraftingPatternContainer getContainer();

    /**
     * @return The crafting pattern stack
     */
    ItemStack getStack();

    /**
     * @return Whether the crafting pattern is valid
     */
    boolean isValid();

    /**
     * @return The inputs
     */
    List<ItemStack> getInputs();

    /**
     * @return The outputs
     */
    List<ItemStack> getOutputs();

    /**
     * @return The id of the crafting task, as defined in the registry
     */
    String getId();

    /**
     * @param requested The item requested
     * @return The quantity returned per request
     */
    int getQuantityPerRequest(ItemStack requested);
}
