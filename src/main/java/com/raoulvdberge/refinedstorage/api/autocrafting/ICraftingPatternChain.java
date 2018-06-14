package com.raoulvdberge.refinedstorage.api.autocrafting;

public interface ICraftingPatternChain {
    ICraftingPattern current();

    ICraftingPattern cycle();
}
