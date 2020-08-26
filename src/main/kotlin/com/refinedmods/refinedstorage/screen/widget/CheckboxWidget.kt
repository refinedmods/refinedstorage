package com.refinedmods.refinedstorage.screen.widget

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.widget.button.CheckboxButton
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import java.util.function.Consumer

class CheckboxWidget(x: Int, y: Int, text: Text, isChecked: Boolean, onPress: Consumer<CheckboxButton>) : CheckboxButton(
        x,
        y,
        Minecraft.getInstance().fontRenderer.getStringWidth(text.getString()) + BOX_WIDTH,
        10,
        text,
        isChecked
) {
    private val onPress: Consumer<CheckboxButton>
    private var shadow = true
    fun setShadow(shadow: Boolean) {
        this.shadow = shadow
    }

    fun onPress() {
        super.onPress()
        onPress.accept(this)
    }

    fun setChecked(value: Boolean) {
        this.checked = value
    }

    fun renderButton(matrixStack: MatrixStack?, p_230431_2_: Int, p_230431_3_: Int, p_230431_4_: Float) {
        val minecraft: Minecraft = Minecraft.getInstance()
        minecraft.getTextureManager().bindTexture(TEXTURE)
        RenderSystem.enableDepthTest()
        val fontrenderer: FontRenderer = minecraft.fontRenderer
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        val textureX = if (this.isFocused()) 10.0f else 0.0f
        val textureY = if (this.checked) 10.0f else 0.0f
        val width = 10
        val height = 10
        blit(matrixStack, this.x, this.y, textureX, textureY, width, height, 32, 32)
        this.renderBg(matrixStack, minecraft, p_230431_2_, p_230431_3_)
        var color = 14737632
        if (!active) {
            color = 10526880
        } else if (packedFGColor !== 0) {
            color = packedFGColor
        }
        if (shadow) {
            super.drawString(matrixStack, fontrenderer, this.getMessage(), this.x + 13, this.y + (height - 8) / 2, color)
        } else {
            fontrenderer.drawString(matrixStack, this.getMessage().getString(), this.x + 13, this.y + (height - 8) / 2f, color)
        }
    }

    companion object {
        private val TEXTURE: Identifier = Identifier("textures/gui/checkbox.png")
        private const val BOX_WIDTH = 13
    }

    init {
        this.onPress = onPress
    }
}