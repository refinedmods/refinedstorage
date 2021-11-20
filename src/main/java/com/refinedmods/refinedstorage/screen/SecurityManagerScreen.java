package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.SecurityManagerContainer;
import com.refinedmods.refinedstorage.item.SecurityCardItem;
import com.refinedmods.refinedstorage.network.SecurityManagerUpdateMessage;
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.SecurityManagerTile;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SecurityManagerScreen extends BaseScreen<SecurityManagerContainer> {
    private final SecurityManagerTile securityManager;
    private final CheckboxWidget[] permissions = new CheckboxWidget[Permission.values().length];

    public SecurityManagerScreen(SecurityManagerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 234, inventory, title);

        this.securityManager = (SecurityManagerTile) container.getTile();
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));

        int padding = 15;

        permissions[0] = addCheckBox(x + 7, y + 93, new TranslationTextComponent("gui.refinedstorage.security_manager.permission.0"), false, btn -> handle(0));
        permissions[1] = addCheckBox(permissions[0].x, permissions[0].y + padding, new TranslationTextComponent("gui.refinedstorage.security_manager.permission.1"), false, btn -> handle(1));
        permissions[2] = addCheckBox(permissions[1].x, permissions[1].y + padding, new TranslationTextComponent("gui.refinedstorage.security_manager.permission.2"), false, btn -> handle(2));
        permissions[3] = addCheckBox(permissions[0].x + 90, permissions[0].y, new TranslationTextComponent("gui.refinedstorage.security_manager.permission.3"), false, btn -> handle(3));
        permissions[4] = addCheckBox(permissions[3].x, permissions[3].y + padding, new TranslationTextComponent("gui.refinedstorage.security_manager.permission.4"), false, btn -> handle(4));
        permissions[5] = addCheckBox(permissions[4].x, permissions[4].y + padding, new TranslationTextComponent("gui.refinedstorage.security_manager.permission.5"), false, btn -> handle(5));
    }

    private void handle(int i) {
        RS.NETWORK_HANDLER.sendToServer(new SecurityManagerUpdateMessage(securityManager.getPos(), Permission.values()[i], permissions[i].isChecked()));
    }

    @Override
    public void tick(int x, int y) {
        ItemStack card = securityManager.getNode().getEditCard().getStackInSlot(0);

        for (Permission permission : Permission.values()) {
            permissions[permission.getId()].setChecked(!card.isEmpty() && SecurityCardItem.hasPermission(card, permission));
        }
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/security_manager.png");

        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 59, I18n.format("gui.refinedstorage.security_manager.configure"));
        renderString(matrixStack, 7, 140, I18n.format("container.inventory"));

        for (int i = 0; i < permissions.length; ++i) {
            CheckboxWidget permission = permissions[i];

            // getWidth_CLASH => getHeight
            if (RenderUtils.inBounds(permission.x - guiLeft, permission.y - guiTop, permission.getWidth(), permission.getHeight(), mouseX, mouseY)) {
                renderTooltip(matrixStack, mouseX, mouseY, I18n.format("gui.refinedstorage.security_manager.permission." + i + ".tooltip"));
            }
        }
    }
}
