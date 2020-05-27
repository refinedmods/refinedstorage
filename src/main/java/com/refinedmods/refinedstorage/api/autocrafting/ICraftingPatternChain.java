package com.refinedmods.refinedstorage.api.autocrafting;

/**
 * A crafting pattern chain, which stores equivalent patterns.
 */
public interface ICraftingPatternChain {
    /**
     * @return the current pattern in the chain
     */
    ICraftingPattern current();

    /**
     * Cycles the pattern in the chain.
     *
     * @return the cycled (and now current) pattern
     */
    ICraftingPattern cycle();
}
