package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.text.StringTextComponent
import org.lwjgl.opengl.GL11

abstract class SideButton(protected val screen: BaseScreen<*>) : Button(-1, -1, 18, 18, StringTextComponent(""), { btn -> }) {
    fun renderButton(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableAlphaTest()
        isHovered = RenderUtils.inBounds(x, y, width, height, mouseX.toDouble(), mouseY.toDouble())
        screen.bindTexture(RS.ID, "icons.png")
        screen.blit(matrixStack, x, y, 238, if (isHovered) 35 else 16, 18, 18)
        renderButtonIcon(matrixStack, x + 1, y + 1)
        if (isHovered) {
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.5f)
            screen.blit(matrixStack, x, y, 238, 54, 18, 18)
            RenderSystem.disableBlend()
        }
    }

    protected abstract fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int)
    abstract fun getTooltip(): String

    companion object {
        const val WIDTH = 18
        const val HEIGHT = 18
    }
}