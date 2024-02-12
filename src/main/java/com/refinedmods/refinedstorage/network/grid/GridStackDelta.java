package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

public record GridStackDelta<T extends IGridStack>(int change, T stack) {
}
