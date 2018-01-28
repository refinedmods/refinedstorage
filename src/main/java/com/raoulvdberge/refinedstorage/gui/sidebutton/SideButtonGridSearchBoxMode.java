package com.raoulvdberge.refinedstorage.gui.sidebutton;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.integration.jei.IntegrationJEI;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class SideButtonGridSearchBoxMode extends SideButton {
    public SideButtonGridSearchBoxMode(GuiGrid gui) {
        super(gui);
    }

    @Override
    public String getTooltip() {
        return GuiBase.t("sidebutton.refinedstorage:grid.search_box_mode") + "\n" + TextFormatting.GRAY + GuiBase.t("sidebutton.refinedstorage:grid.search_box_mode." + ((GuiGrid) gui).getGrid().getSearchBoxMode());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        int mode = ((GuiGrid) gui).getGrid().getSearchBoxMode();
        int offset = 0;
        
        offset = mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED ? 16 : offset;
        offset = mode == IGrid.SEARCH_BOX_MODE_NORMAL_KEEP || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_KEEP ? 32 : offset;
        offset = mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED_KEEP || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED_KEEP ? 48 : offset;

        gui.drawTexture(x, y, offset, 96, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int mode = ((GuiGrid) gui).getGrid().getSearchBoxMode();
        
        if (GuiScreen.isShiftKeyDown()) {
            if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED_KEEP) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_KEEP) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED_KEEP) mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED) mode = IGrid.SEARCH_BOX_MODE_NORMAL_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_KEEP) mode = IGrid.SEARCH_BOX_MODE_NORMAL;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED_KEEP;
            
            if (!IntegrationJEI.isLoaded() && IGrid.isSearchBoxModeWithJEISynchronized(mode)) {
                mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED_KEEP;
            }
        } else {
            if (mode == IGrid.SEARCH_BOX_MODE_NORMAL) mode = IGrid.SEARCH_BOX_MODE_NORMAL_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_KEEP) mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED) mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED_KEEP) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_KEEP) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED_KEEP;
            else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED_KEEP) mode = IGrid.SEARCH_BOX_MODE_NORMAL;
            
            if (!IntegrationJEI.isLoaded() && IGrid.isSearchBoxModeWithJEISynchronized(mode)) {
                mode = IGrid.SEARCH_BOX_MODE_NORMAL;
            }
        }

        ((GuiGrid) gui).getGrid().onSearchBoxModeChanged(mode);

        ((GuiGrid) gui).updateSearchFieldFocus(mode);
    }
}
