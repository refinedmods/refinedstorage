package com.refinedmods.refinedstorage.screen.grid.view;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IGridView {
    List<IGridStack> getStacks();

    @Nullable
    IGridStack get(UUID id);

    Collection<IGridStack> getAllStacks();

    void setStacks(List<IGridStack> stacks);

    void postChange(IGridStack stack, int delta);

    void setCanCraft(boolean canCraft);

    boolean canCraft();

    void sort();
}
