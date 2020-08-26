package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.container.SecurityManagerContainer
import com.refinedmods.refinedstorage.item.SecurityCardItem.Companion.hasPermission
import com.refinedmods.refinedstorage.network.SecurityManagerUpdateMessage
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.SecurityManagerTile
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent

class SecurityManagerScreen(container: SecurityManagerContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<SecurityManagerContainer?>(container, 176, 234, inventory, title) {
    private val securityManager: SecurityManagerTile?
    private val permissions = arrayOfNulls<CheckboxWidget>(Permission.values().size)
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, SecurityManagerTile.REDSTONE_MODE))
        val padding = 15
        permissions[0] = addCheckBox(x + 7, y + 93, TranslationTextComponent("gui.refinedstorage.security_manager.permission.0"), false) { btn: CheckboxButton? -> handle(0) }
        permissions[1] = addCheckBox(permissions[0].x, permissions[0].y + padding, TranslationTextComponent("gui.refinedstorage.security_manager.permission.1"), false) { btn: CheckboxButton? -> handle(1) }
        permissions[2] = addCheckBox(permissions[1].x, permissions[1].y + padding, TranslationTextComponent("gui.refinedstorage.security_manager.permission.2"), false) { btn: CheckboxButton? -> handle(2) }
        permissions[3] = addCheckBox(permissions[0].x + 90, permissions[0].y, TranslationTextComponent("gui.refinedstorage.security_manager.permission.3"), false) { btn: CheckboxButton? -> handle(3) }
        permissions[4] = addCheckBox(permissions[3].x, permissions[3].y + padding, TranslationTextComponent("gui.refinedstorage.security_manager.permission.4"), false) { btn: CheckboxButton? -> handle(4) }
        permissions[5] = addCheckBox(permissions[4].x, permissions[4].y + padding, TranslationTextComponent("gui.refinedstorage.security_manager.permission.5"), false) { btn: CheckboxButton? -> handle(5) }
    }

    private fun handle(i: Int) {
        RS.NETWORK_HANDLER.sendToServer(SecurityManagerUpdateMessage(securityManager!!.pos, Permission.values()[i], permissions[i].isChecked()))
    }

    override fun tick(x: Int, y: Int) {
        val card: ItemStack = securityManager.getNode().getEditCard().getStackInSlot(0)
        for (permission in Permission.values()) {
            permissions[permission.id]!!.setChecked(!card.isEmpty && hasPermission(card, permission))
        }
    }

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/security_manager.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 59, I18n.format("gui.refinedstorage.security_manager.configure"))
        renderString(matrixStack, 7, 140, I18n.format("container.inventory"))
        for (i in permissions.indices) {
            val permission = permissions[i]

            // getWidth_CLASH => getHeight
            if (RenderUtils.inBounds(permission.x - guiLeft, permission.y - guiTop, permission.getWidth(), permission.getWidth_CLASH(), mouseX.toDouble(), mouseY.toDouble())) {
                renderTooltip(matrixStack, mouseX, mouseY, I18n.format("gui.refinedstorage.security_manager.permission.$i.tooltip"))
            }
        }
    }

    init {
        securityManager = container.tile as SecurityManagerTile?
    }
}