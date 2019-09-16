package com.raoulvdberge.refinedstorage.screen.widget.sidebutton;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.screen.GuiCrafterManager;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class SideButtonCrafterManagerSearchBoxMode extends SideButton {
    public SideButtonCrafterManagerSearchBoxMode(GuiCrafterManager gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:grid.search_box_mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:grid.search_box_mode." + ((GuiCrafterManager) screen).getCrafterManager().getSearchBoxMode());
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        int mode = ((GuiCrafterManager) screen).getCrafterManager().getSearchBoxMode();

        screen.blit(x, y, mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ? 16 : 0, 96, 16, 16);
    }

    @Override
    public void onPress() {
        int mode = ((GuiCrafterManager) screen).getCrafterManager().getSearchBoxMode();

        if (mode == IGrid.SEARCH_BOX_MODE_NORMAL) {
            mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED;
        } else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED) {
            //TODO if (IntegrationJEI.isLoaded()) {
            //    mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED;
            //} else {
                mode = IGrid.SEARCH_BOX_MODE_NORMAL;
            //}
        } else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
            mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
        } else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) {
            mode = IGrid.SEARCH_BOX_MODE_NORMAL;
        }

        TileDataManager.setParameter(TileCrafterManager.SEARCH_BOX_MODE, mode);
    }
}
