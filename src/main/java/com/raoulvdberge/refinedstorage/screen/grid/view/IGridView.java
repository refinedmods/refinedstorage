package com.raoulvdberge.refinedstorage.screen.grid.view;

import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IGridView {
    List<IGridStack> getStacks();

    @Nullable
    IGridStack get(UUID id);

    void setStacks(List<IGridStack> stacks);

    void postChange(IGridStack stack, int delta);

    void setCanCraft(boolean canCraft);

    boolean canCraft();

    void sort();
}
