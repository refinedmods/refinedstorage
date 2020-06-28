package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.ExporterTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CraftOnlySideButton extends SideButton {
    public CraftOnlySideButton(BaseScreen<?> screen) {
        super(screen);
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, (ExporterTile.CRAFT_ONLY.getValue() ? 16 : 0), 144, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.craft_only") + "\n" + TextFormatting.GRAY + I18n.format(ExporterTile.CRAFT_ONLY.getValue() ? "sidebutton.refinedstorage.craft_only.yes" : "sidebutton.refinedstorage.craft_only.no");
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(ExporterTile.CRAFT_ONLY, !ExporterTile.CRAFT_ONLY.getValue());
    }
}
