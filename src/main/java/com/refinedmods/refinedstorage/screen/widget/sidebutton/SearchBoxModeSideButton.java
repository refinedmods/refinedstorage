package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.integration.jei.JeiIntegration;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public abstract class SearchBoxModeSideButton extends SideButton {
    private static final List<Integer> MODE_ROTATION = Arrays.asList(
            IGrid.SEARCH_BOX_MODE_NORMAL,
            IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED,
            IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED,
            IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED,
            IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY,
            IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_2WAY_AUTOSELECTED,
            IGrid.SEARCH_BOX_MODE_NORMAL
    );

    private static int nextMode(int oldMode) {
        return MODE_ROTATION.get(MODE_ROTATION.indexOf(oldMode) + 1);
    }

    protected SearchBoxModeSideButton(BaseScreen<?> screen) {
        super(screen);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.grid.search_box_mode") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.search_box_mode." + getSearchBoxMode());
    }

    @Override
    protected void renderButtonIcon(MatrixStack matrixStack, int x, int y) {
        int mode = getSearchBoxMode();

        screen.blit(matrixStack, x, y, IGrid.isSearchBoxModeWithAutoselection(mode) ? 16 : 0, 96, 16, 16);
    }

    @Override
    public void onPress() {
        int mode = nextMode(getSearchBoxMode());

        if (IGrid.doesSearchBoxModeUseJEI(mode) && !JeiIntegration.isLoaded()) {
            mode = IGrid.SEARCH_BOX_MODE_NORMAL;
        }

        setSearchBoxMode(mode);
    }

    protected abstract int getSearchBoxMode();

    protected abstract void setSearchBoxMode(int mode);
}
