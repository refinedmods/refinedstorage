package com.raoulvdberge.refinedstorage.gui.grid.view;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;

import java.util.List;

public interface IGridView {
    List<IGridStack> getStacks();

    void setStacks(List<IGridStack> stacks);

    void postChange(IGridStack stack, int delta);

    void setCanCraft(boolean canCraft);

    boolean canCraft();

    void sort();
}
