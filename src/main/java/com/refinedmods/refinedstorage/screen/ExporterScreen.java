package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.ExporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.container.ExporterContainerMenu;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExporterScreen extends BaseScreen<ExporterContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RS.ID, "textures/gui/exporter.png");

    private boolean hasRegulatorMode;

    public ExporterScreen(ExporterContainerMenu containerMenu, Inventory playerInventory, Component title) {
        super(containerMenu, 211, 137, playerInventory, title);

        this.hasRegulatorMode = hasRegulatorMode();
    }

    private boolean hasRegulatorMode() {
        return ((ExporterBlockEntity) menu.getBlockEntity()).getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, ExporterBlockEntity.TYPE));

        addSideButton(new ExactModeSideButton(this, ExporterBlockEntity.COMPARE));
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
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());
        renderString(graphics, 7, 43, I18n.get("container.inventory"));
    }
}
