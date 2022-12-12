package com.refinedmods.refinedstorage.screen.grid.view;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface IGridView {
    List<IGridStack> getStacks();

    void setStacks(List<IGridStack> stacks);

    @Nullable
    IGridStack get(UUID id);

    Collection<IGridStack> getAllStacks();

    void postChange(IGridStack stack, int delta);

    void setCanCraft(boolean canCraft);

    boolean canCraft();

    void sort();

    void addDeltaListener(Consumer<IGridStack> listener);

    void removed();
}
