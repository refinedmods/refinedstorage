package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.integration.inventorytweaks.InventoryTweaksIntegration;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GridSortingTypeSideButton extends SideButton {
    private final IGrid grid;

    public GridSortingTypeSideButton(BaseScreen<GridContainer> screen, IGrid grid) {
        super(screen);

        this.grid = grid;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.grid.sorting.type") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.sorting.type." + grid.getSortingType());
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        if (grid.getSortingType() == IGrid.SORTING_TYPE_LAST_MODIFIED) {
            screen.blit(x, y, 48, 48, 16, 16);
        } else {
            screen.blit(x, y, grid.getSortingType() * 16, 32, 16, 16);
        }
    }

    @Override
    public void onPress() {
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
        } else if (type == GridNetworkNode.SORTING_TYPE_LAST_MODIFIED) {
            if (grid.getGridType() == GridType.FLUID || !InventoryTweaksIntegration.isLoaded()) {
                type = IGrid.SORTING_TYPE_QUANTITY;
            } else {
                type = IGrid.SORTING_TYPE_INVENTORYTWEAKS;
            }
        } else if (type == GridNetworkNode.SORTING_TYPE_INVENTORYTWEAKS) {
            type = IGrid.SORTING_TYPE_QUANTITY;
        }

        grid.onSortingTypeChanged(type);
    }
}
