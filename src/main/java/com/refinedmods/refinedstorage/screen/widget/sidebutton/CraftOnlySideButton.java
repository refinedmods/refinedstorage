package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CraftOnlySideButton extends SideButton {
    private final TileDataParameter<Boolean, ?> parameter;

    private boolean hasCraftingUpgrade;

    public CraftOnlySideButton(BaseScreen<?> screen, TileDataParameter<Boolean, ?> parameter, boolean hasCraftingUpgrade) {
        super(screen);

        this.parameter = parameter;
        this.hasCraftingUpgrade = hasCraftingUpgrade;
        this.updateVisibility();
    }

    public void tick(boolean hasCraftingUpgrade) {
        if (this.hasCraftingUpgrade != hasCraftingUpgrade) {
            this.hasCraftingUpgrade = hasCraftingUpgrade;
            this.updateVisibility();
        }
    }

    private void updateVisibility() {
        this.visible = hasCraftingUpgrade;
    }

    @Override
    protected void renderButtonIcon(int x, int y) {
        screen.blit(x, y, (parameter.getValue() ? 16 : 0), 144, 16, 16);
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage.craft_only") + "\n" + TextFormatting.GRAY + I18n.format(parameter.getValue() ? "sidebutton.refinedstorage.craft_only.yes" : "sidebutton.refinedstorage.craft_only.no");
    }

    @Override
    public void onPress() {
        TileDataManager.setParameter(parameter, !parameter.getValue());
    }
}
