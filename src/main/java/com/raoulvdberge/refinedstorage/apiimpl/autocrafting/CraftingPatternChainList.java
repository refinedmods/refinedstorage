package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChain;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChainList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingPatternChainList implements ICraftingPatternChainList {
    /*private Map<ICraftingPattern, CraftingPatternChain> map = new TCustomHashMap<>(new HashingStrategy<ICraftingPattern>() {
        @Override
        public int computeHashCode(ICraftingPattern pattern) {
            return pattern.getChainHashCode();
        }

        @Override
        public boolean equals(ICraftingPattern left, ICraftingPattern right) {
            return left.canBeInChainWith(right);
        }
    });*/
    // TODO: broken
    private Map<ICraftingPattern, CraftingPatternChain> map = new HashMap<>();

    public CraftingPatternChainList(List<ICraftingPattern> patterns) {
        for (ICraftingPattern pattern : patterns) {
            CraftingPatternChain chain = map.get(pattern);
            if (chain == null) {
                map.put(pattern, chain = new CraftingPatternChain());
            }

            chain.addPattern(pattern);
        }
    }

    @Override
    public ICraftingPatternChain getChain(ICraftingPattern pattern) {
        ICraftingPatternChain chain = map.get(pattern);
        if (chain == null) {
            throw new IllegalStateException("Pattern was not found in pattern chain");
        }

        return chain;
    }
}
