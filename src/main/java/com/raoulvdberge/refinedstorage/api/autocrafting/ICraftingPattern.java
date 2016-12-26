package com.raoulvdberge.refinedstorage.api.autocrafting;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
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
     * @return true if the crafting pattern can be treated as a processing pattern, false otherwise
     */
    boolean isProcessing();

    /**
     * @return true if the crafting pattern is oredicted, false otherwise
     */
    boolean isOredict();

    /**
     * TODO
     */
    boolean isBlockingTask();

    /**
     * @return the inputs, can contain nulls
     */
    List<ItemStack> getInputs();

    /**
     * @return the possible inputs per slot, empty list means null slot
     */
    List<List<ItemStack>> getOreInputs();

    /**
     * @param took the items took
     * @return the outputs based on the items took, null when failed
     */
    @Nullable
    List<ItemStack> getOutputs(ItemStack[] took);

    /**
     * @return the outputs
     */
    List<ItemStack> getOutputs();

    /**
     * @param took the items took
     * @return the outputs based on the items took
     */
    List<ItemStack> getByproducts(ItemStack[] took);

    /**
     * @return the byproducts
     */
    List<ItemStack> getByproducts();

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
    default int getQuantityPerRequest(ItemStack requested) {
        return getQuantityPerRequest(requested, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
    }

    /**
     * Returns the quantity of items that this crafting task yields per request.
     *
     * @param requested the item requested
     * @param compare   the {@link IComparer} flags
     * @return the quantity
     */
    int getQuantityPerRequest(ItemStack requested, int compare);

    /**
     * Returns the actual outputted {@link ItemStack}
     *
     * @param requested an item requested
     * @param compare   the {@link IComparer} flags
     * @return the actual {@link ItemStack} with quantity
     */
    ItemStack getActualOutput(ItemStack requested, int compare);
}
