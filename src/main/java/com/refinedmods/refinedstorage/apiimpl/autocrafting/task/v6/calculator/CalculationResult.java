package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CalculationResult implements ICalculationResult {
    private final CalculationResultType type;
    private final ICraftingPattern recursedPattern;
    private final List<ICraftingPreviewElement<?>> previewElements;
    private final ICraftingTask craftingTask;

    public CalculationResult(CalculationResultType type) {
        this.type = type;
        this.recursedPattern = null;
        this.previewElements = Collections.emptyList();
        this.craftingTask = null;
    }

    public CalculationResult(CalculationResultType type, ICraftingPattern recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
        this.previewElements = Collections.emptyList();
        this.craftingTask = null;
    }

    public CalculationResult(CalculationResultType type, List<ICraftingPreviewElement<?>> previewElements, @Nullable ICraftingTask craftingTask) {
        this.type = type;
        this.recursedPattern = null;
        this.previewElements = previewElements;
        this.craftingTask = craftingTask;
    }

    @Override
    public CalculationResultType getType() {
        return type;
    }

    @Override
    public List<ICraftingPreviewElement<?>> getPreviewElements() {
        return previewElements;
    }

    @Nullable
    @Override
    public ICraftingTask getTask() {
        return craftingTask;
    }

    @Override
    public boolean isOk() {
        return type == CalculationResultType.OK;
    }

    @Override
    @Nullable
    public ICraftingPattern getRecursedPattern() {
        return recursedPattern;
    }
}
