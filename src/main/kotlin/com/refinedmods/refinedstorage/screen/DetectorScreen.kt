package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.DetectorContainer
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.widget.sidebutton.DetectorModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton
import com.refinedmods.refinedstorage.tile.DetectorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import org.lwjgl.glfw.GLFW

class DetectorScreen(container: DetectorContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<DetectorContainer?>(container, 176, 137, inventory, title) {
    private var amountField: TextFieldWidget? = null
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(TypeSideButton(this, DetectorTile.TYPE))
        addSideButton(DetectorModeSideButton(this))
        addSideButton(ExactModeSideButton(this, DetectorTile.COMPARE))
        amountField = TextFieldWidget(font, x + 41 + 1, y + 23 + 1, 50, font.FONT_HEIGHT, StringTextComponent(""))
        amountField!!.text = DetectorTile.AMOUNT.value.toString()
        amountField.setEnableBackgroundDrawing(false)
        amountField!!.isVisible = true
        amountField.setCanLoseFocus(true)
        amountField.setFocused2(false)
        amountField.setTextColor(RenderSettings.INSTANCE.secondaryColor)
        amountField.setResponder { value ->
            try {
                val result: Int = value.toInt()
                TileDataManager.setParameter(DetectorTile.AMOUNT, result)
            } catch (e: NumberFormatException) {
                // NO OP
            }
        }
        addButton(amountField)
    }

    fun updateAmountField(amount: Int) {
        amountField!!.text = amount.toString()
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/detector.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"))
    }

    fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.player.closeScreen()
            return true
        }
        return if (amountField!!.keyPressed(key, scanCode, modifiers) || amountField.canWrite()) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
    }
}