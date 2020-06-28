package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.ExporterContainer;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.ExporterTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ExporterScreen extends BaseScreen<ExporterContainer> {
    private boolean hasRegulatorUpgrade;
    private boolean hasCraftingUpgrade;
    private SideButton craftOnlyButton;

    public ExporterScreen(ExporterContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 137, playerInventory, title);

        this.hasRegulatorUpgrade = hasRegulatorUpgrade();
        this.hasCraftingUpgrade = hasCraftingUpgrade();
    }

    private boolean hasRegulatorUpgrade() {
        return ((ExporterTile) container.getTile()).getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    private boolean hasCraftingUpgrade() {
        return ((ExporterTile) container.getTile()).getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.CRAFTING);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, ExporterTile.REDSTONE_MODE));
        addSideButton(new TypeSideButton(this, ExporterTile.TYPE));
        addSideButton(new ExactModeSideButton(this, ExporterTile.COMPARE));
        addSideButton(craftOnlyButton = new CraftOnlySideButton(this));
        craftOnlyButton.visible = hasCraftingUpgrade;
    }

    @Override
    public void tick(int x, int y) {
        boolean updatedHasRegulatorMode = hasRegulatorUpgrade();
        if (hasRegulatorUpgrade != updatedHasRegulatorMode) {
            hasRegulatorUpgrade = updatedHasRegulatorMode;

            container.initSlots();
        }

        boolean updatedHasCraftingUpgrade = hasCraftingUpgrade();
        if (hasCraftingUpgrade != updatedHasCraftingUpgrade) {
            hasCraftingUpgrade = updatedHasCraftingUpgrade;

            craftOnlyButton.visible = hasCraftingUpgrade;
        }
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/exporter.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
