package com.raoulvdberge.refinedstorage.gui.control;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiCrafterManager;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.util.text.TextFormatting;

public class SideButtonCrafterManagerSearchBoxMode extends SideButton {
    public SideButtonCrafterManagerSearchBoxMode(GuiCrafterManager gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:grid.search_box_mode") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:grid.search_box_mode." + ((GuiCrafterManager) gui).getCrafterManager().getSearchBoxMode());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        int mode = ((GuiCrafterManager) gui).getCrafterManager().getSearchBoxMode();

        gui.drawTexture(x, y, mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ? 16 : 0, 96, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int mode = ((GuiCrafterManager) gui).getCrafterManager().getSearchBoxMode();

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
