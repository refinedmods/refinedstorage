package com.raoulvdberge.refinedstorage.gui.control;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.sorting.GridSorterInventoryTweaks;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public class SideButtonGridSortingType extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingType(GuiBase gui, IGrid grid) {
        super(gui);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:grid.sorting.type") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:grid.sorting.type." + grid.getSortingType());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        if (grid.getSortingType() == IGrid.SORTING_TYPE_LAST_MODIFIED) {
            gui.drawTexture(x, y, 48, 48, 16, 16);
        } else {
            gui.drawTexture(x, y, grid.getSortingType() * 16, 32, 16, 16);
        }
    }

    @Override
    public void actionPerformed() {
        int type = grid.getSortingType();

        if (type == IGrid.SORTING_TYPE_QUANTITY) {
            type = IGrid.SORTING_TYPE_NAME;
        } else if (type == IGrid.SORTING_TYPE_NAME) {
            if (grid.getGridType() == GridType.FLUID) {
                type = IGrid.SORTING_TYPE_LAST_MODIFIED;
            } else {
                type = IGrid.SORTING_TYPE_ID;
            }
        } else if (type == IGrid.SORTING_TYPE_ID) {
            type = IGrid.SORTING_TYPE_LAST_MODIFIED;
        } else if (type == NetworkNodeGrid.SORTING_TYPE_LAST_MODIFIED) {
            if (grid.getGridType() == GridType.FLUID || !Loader.isModLoaded(GridSorterInventoryTweaks.MOD_ID)) {
                type = IGrid.SORTING_TYPE_QUANTITY;
            } else {
                type = IGrid.SORTING_TYPE_INVENTORYTWEAKS;
            }
        } else if (type == NetworkNodeGrid.SORTING_TYPE_INVENTORYTWEAKS) {
            type = IGrid.SORTING_TYPE_QUANTITY;
        }

        grid.onSortingTypeChanged(type);
    }
}
