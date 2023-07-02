package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class GridViewTypeSideButton extends SideButton {
    private final IGrid grid;

    public GridViewTypeSideButton(BaseScreen<GridContainerMenu> screen, IGrid grid) {
        super(screen);

        this.grid = grid;
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.view_type") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.grid.view_type." + grid.getViewType());
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, (grid.getViewType() - (grid.getViewType() >= 3 ? 3 : 0)) * 16, 112, 16, 16);
    }

    @Override
    public void onPress() {
        int type = grid.getViewType();

        if (type == IGrid.VIEW_TYPE_NORMAL) {
            type = IGrid.VIEW_TYPE_NON_CRAFTABLES;
        } else if (type == IGrid.VIEW_TYPE_NON_CRAFTABLES) {
            type = IGrid.VIEW_TYPE_CRAFTABLES;
        } else if (type == IGrid.VIEW_TYPE_CRAFTABLES) {
            type = IGrid.VIEW_TYPE_NORMAL;
        }

        grid.onViewTypeChanged(type);
    }
}
