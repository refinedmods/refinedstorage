package com.refinedmods.refinedstorage.api.autocrafting;

/**
 * A list of pattern chains per pattern.
 */
public interface ICraftingPatternChainList {
    /**
     * @param pattern the pattern
     * @return a chain for the pattern
     */
    ICraftingPatternChain getChain(ICraftingPattern pattern);
}
