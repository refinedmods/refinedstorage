package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternChain;

import java.util.ArrayList;
import java.util.List;

public class CraftingPatternChain implements ICraftingPatternChain {
    private List<ICraftingPattern> patterns = new ArrayList<>();
    private int pos;

    public void addPattern(ICraftingPattern pattern) {
        patterns.add(pattern);
    }

    @Override
    public ICraftingPattern current() {
        return patterns.get(pos);
    }

    @Override
    public ICraftingPattern cycle() {
        if (pos + 1 >= patterns.size()) {
            this.pos = 0;
        } else {
            this.pos++;
        }

        return current();
    }
}
