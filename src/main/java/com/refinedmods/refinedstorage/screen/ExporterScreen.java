package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.ExporterContainer;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.tile.ExporterTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ExporterScreen extends BaseScreen<ExporterContainer> {
    private boolean hasRegulatorMode;

    public ExporterScreen(ExporterContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 137, playerInventory, title);

        this.hasRegulatorMode = hasRegulatorMode();
    }

    private boolean hasRegulatorMode() {
        return ((ExporterTile) menu.getTile()).getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, ExporterTile.TYPE));

        addSideButton(new ExactModeSideButton(this, ExporterTile.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode) {
            hasRegulatorMode = updatedHasRegulatorMode;

            menu.initSlots();
        }
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/exporter.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.get("container.inventory"));
    }
}
