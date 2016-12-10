package com.raoulvdberge.refinedstorage.gui.grid;


public class GridDisplayDummy implements IGridDisplay {
    @Override
    public int getVisibleRows() {
        return 3;
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public int getHeader() {
        return 0;
    }

    @Override
    public int getFooter() {
        return 0;
    }

    @Override
    public int getYPlayerInventory() {
        return 0;
    }
}
