package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;

public interface ICraftingStep {
    boolean execute();

    boolean canExecute();

    ICraftingPattern getPattern();
}
