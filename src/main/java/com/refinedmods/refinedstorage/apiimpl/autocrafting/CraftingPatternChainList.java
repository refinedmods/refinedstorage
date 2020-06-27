package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternChain;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternChainList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CraftingPatternChainList implements ICraftingPatternChainList {
    private final Map<Key, CraftingPatternChain> map = new HashMap<>();

    public CraftingPatternChainList(Collection<ICraftingPattern> patterns) {
        for (ICraftingPattern pattern : patterns) {
            Key key = new Key(pattern);

            CraftingPatternChain chain = map.get(key);

            if (chain == null) {
                map.put(key, chain = new CraftingPatternChain());
            }

            chain.addPattern(pattern);
        }
    }

    @Override
    public ICraftingPatternChain getChain(ICraftingPattern pattern) {
        ICraftingPatternChain chain = map.get(new Key(pattern));

        if (chain == null) {
            throw new IllegalStateException("Pattern was not found in pattern chain");
        }

        return chain;
    }

    private static class Key {
        private final ICraftingPattern pattern;

        public Key(ICraftingPattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Key && pattern.canBeInChainWith(((Key) other).pattern);
        }

        @Override
        public int hashCode() {
            return pattern.getChainHashCode();
        }
    }
}
