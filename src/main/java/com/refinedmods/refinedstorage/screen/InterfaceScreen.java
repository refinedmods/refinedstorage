package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.InterfaceContainer;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.CraftOnlySideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.tile.InterfaceTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class InterfaceScreen extends BaseScreen<InterfaceContainer> {
    private boolean hasCraftingUpgrade;
    private SideButton craftOnlyButton;

    public InterfaceScreen(InterfaceContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 217, inventory, title);
    }

    private boolean hasCraftingUpgrade() {
        return ((InterfaceTile) container.getTile()).getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.CRAFTING);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, InterfaceTile.REDSTONE_MODE));
        addSideButton(new ExactModeSideButton(this, InterfaceTile.COMPARE));
        addSideButton(craftOnlyButton = new CraftOnlySideButton(this, InterfaceTile.CRAFT_ONLY));
        craftOnlyButton.visible = hasCraftingUpgrade;
    }

    @Override
    public void tick(int x, int y) {
        boolean updatedHasCraftingUpgrade = hasCraftingUpgrade();
        if (hasCraftingUpgrade != updatedHasCraftingUpgrade) {
            hasCraftingUpgrade = updatedHasCraftingUpgrade;

            craftOnlyButton.visible = hasCraftingUpgrade;
        }
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/interface.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage.interface.import"));
        renderString(7, 42, I18n.format("gui.refinedstorage.interface.export"));
        renderString(7, 122, I18n.format("container.inventory"));
    }
}
