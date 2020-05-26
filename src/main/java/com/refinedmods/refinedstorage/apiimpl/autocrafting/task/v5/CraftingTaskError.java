package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v5;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskError;

import javax.annotation.Nullable;

public class CraftingTaskError implements ICraftingTaskError {
    private CraftingTaskErrorType type;
    private ICraftingPattern recursedPattern;

    public CraftingTaskError(CraftingTaskErrorType type) {
        this.type = type;
    }

    public CraftingTaskError(CraftingTaskErrorType type, ICraftingPattern recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    @Override
    public CraftingTaskErrorType getType() {
        return type;
    }

    @Override
    @Nullable
    public ICraftingPattern getRecursedPattern() {
        return recursedPattern;
    }
}
