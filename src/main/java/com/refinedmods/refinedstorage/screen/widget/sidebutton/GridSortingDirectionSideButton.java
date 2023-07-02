package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class GridSortingDirectionSideButton extends SideButton {
    private final IGrid grid;

    public GridSortingDirectionSideButton(BaseScreen<GridContainerMenu> screen, IGrid grid) {
        super(screen);

        this.grid = grid;
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.sorting.direction") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.grid.sorting.direction." + grid.getSortingDirection());
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, grid.getSortingDirection() * 16, 16, 16, 16);
    }

    @Override
    public void onPress() {
        int dir = grid.getSortingDirection();

        if (dir == IGrid.SORTING_DIRECTION_ASCENDING) {
            dir = IGrid.SORTING_DIRECTION_DESCENDING;
        } else if (dir == IGrid.SORTING_DIRECTION_DESCENDING) {
            dir = IGrid.SORTING_DIRECTION_ASCENDING;
        }

        grid.onSortingDirectionChanged(dir);
    }
}
