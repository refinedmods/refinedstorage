package com.raoulvdberge.refinedstorage.gui.grid.filtering;

import com.raoulvdberge.refinedstorage.gui.grid.stack.IClientStack;

public interface IGridFilter {
    boolean accepts(IClientStack stack);
}
