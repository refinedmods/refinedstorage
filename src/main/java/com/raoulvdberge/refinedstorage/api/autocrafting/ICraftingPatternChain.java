package com.raoulvdberge.refinedstorage.api.autocrafting;

import java.util.Collection;

/**
 * Represents a chain of {@link ICraftingPattern}s, used to balance crafts over those patterns
 */
public interface ICraftingPatternChain extends Collection<ICraftingPattern> {
    /**
     * Check whether a pattern belongs in the chain
     *
     * @param compare the {@link ICraftingPattern} to check
     * @return true if the chains {@link #getPrototype()} is {@link ICraftingPattern#alike(ICraftingPattern)}
     */
    default boolean isValidForChain(ICraftingPattern compare) {
        return getPrototype() == compare || getPrototype().alike(compare);
    }

    /**
     * Cycles the list and returns use you the pattern that was used the longest time ago
     *
     * @return an {@link ICraftingPattern}
     */
    ICraftingPattern cycle();

    /**
     * The prototype used for this {@link ICraftingPatternChain}
     *
     * @return an {@link ICraftingPattern} that represents all patterns in the chain
     */
    ICraftingPattern getPrototype();
}
