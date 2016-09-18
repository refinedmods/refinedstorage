package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Represents a crafting pattern.
 */
public interface ICraftingPattern {
    /**
     * @return the {@link ICraftingPatternContainer} where the pattern is in
     */
    ICraftingPatternContainer getContainer();

    /**
     * @return the crafting pattern stack
     */
    ItemStack getStack();

    /**
     * @return true if the crafting pattern is valid, false otherwise
     */
    boolean isValid();

    /**
     * @return true if this crafting pattern cares about the ore dictionary when extracting items, false otherwise
     */
    boolean isOredicted();

    /**
     * @return the inputs
     */
    List<ItemStack> getInputs();

    /**
     * @return the outputs
     */
    List<ItemStack> getOutputs();

    /**
     * @param took the items that it already took
     * @return the outputs
     */
    List<ItemStack> getOutputsBasedOnTook(ItemStack[] took);

    /**
     * @return the id of the factory that creates a crafting task for this pattern, as defined in the registry
     */
    String getId();

    /**
     * Returns the quantity of items that this crafting task yields per request.
     *
     * @param requested the item requested
     * @return the quantity
     */
    int getQuantityPerRequest(ItemStack requested);
}
