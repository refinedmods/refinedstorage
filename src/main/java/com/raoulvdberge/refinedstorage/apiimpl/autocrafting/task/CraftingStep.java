package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;

public abstract class CraftingStep {
    protected ICraftingPattern pattern;
    private boolean completed;

    public CraftingStep(ICraftingPattern pattern) {
        this.pattern = pattern;
    }

    public abstract boolean canExecute();

    public abstract void execute();

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        this.completed = true;
    }
}
