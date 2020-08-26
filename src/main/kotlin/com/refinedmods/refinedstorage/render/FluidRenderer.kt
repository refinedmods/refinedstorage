package com.refinedmods.refinedstorage.render

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.fluid.Fluid
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.util.Identifier
import net.minecraft.util.math.vector.Matrix4f
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.FluidInstance

/**
 * @link https://github.com/mezz/JustEnoughItems/blob/1.15/src/main/java/mezz/jei/plugins/vanilla/ingredients/fluid/FluidInstanceRenderer.java
 */
class FluidRenderer(private val capacityMb: Int, private val width: Int, private val height: Int, private val minHeight: Int) {
    fun render(matrixStack: MatrixStack, xPosition: Int, yPosition: Int, @Nullable fluidStack: FluidInstance?) {
        RenderSystem.enableBlend()
        RenderSystem.enableAlphaTest()
        drawFluid(matrixStack, xPosition, yPosition, fluidStack)
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        RenderSystem.disableAlphaTest()
        RenderSystem.disableBlend()
    }

    private fun drawFluid(matrixStack: MatrixStack, xPosition: Int, yPosition: Int, @Nullable fluidStack: FluidInstance?) {
        if (fluidStack == null) {
            return
        }
        val fluid: Fluid = fluidStack.getFluid() ?: return
        val fluidStillSprite: TextureAtlasSprite = getStillFluidSprite(fluidStack)
        val attributes: FluidAttributes = fluid.getAttributes()
        val fluidColor: Int = attributes.getColor(fluidStack)
        val amount: Int = fluidStack.getAmount()
        var scaledAmount = amount * height / capacityMb
        if (amount > 0 && scaledAmount < minHeight) {
            scaledAmount = minHeight
        }
        if (scaledAmount > height) {
            scaledAmount = height
        }
        drawTiledSprite(matrixStack, xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite)
    }

    private fun drawTiledSprite(matrixStack: MatrixStack, xPosition: Int, yPosition: Int, tiledWidth: Int, tiledHeight: Int, color: Int, scaledAmount: Int, sprite: TextureAtlasSprite) {
        val minecraft: Minecraft = Minecraft.getInstance()
        minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
        val matrix: Matrix4f = matrixStack.getLast().getMatrix()
        setGLColorFromInt(color)
        val xTileCount = tiledWidth / TEX_WIDTH
        val xRemainder = tiledWidth - xTileCount * TEX_WIDTH
        val yTileCount = scaledAmount / TEX_HEIGHT
        val yRemainder = scaledAmount - yTileCount * TEX_HEIGHT
        val yStart = yPosition + tiledHeight
        for (xTile in 0..xTileCount) {
            for (yTile in 0..yTileCount) {
                val width = if (xTile == xTileCount) xRemainder else TEX_WIDTH
                val height = if (yTile == yTileCount) yRemainder else TEX_HEIGHT
                val x = xPosition + xTile * TEX_WIDTH
                val y = yStart - (yTile + 1) * TEX_HEIGHT
                if (width > 0 && height > 0) {
                    val maskTop = TEX_HEIGHT - height
                    val maskRight = TEX_WIDTH - width
                    drawTextureWithMasking(matrix, x.toFloat(), y.toFloat(), sprite, maskTop, maskRight, 100f)
                }
            }
        }
    }

    companion object {
        @JvmField
        val INSTANCE = FluidRenderer(FluidAttributes.BUCKET_VOLUME, 16, 16, 16)
        private const val TEX_WIDTH = 16
        private const val TEX_HEIGHT = 16
        private fun getStillFluidSprite(fluidStack: FluidInstance): TextureAtlasSprite {
            val minecraft: Minecraft = Minecraft.getInstance()
            val fluid: Fluid = fluidStack.getFluid()
            val attributes: FluidAttributes = fluid.getAttributes()
            val fluidStill: Identifier = attributes.getStillTexture(fluidStack)
            return minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill)
        }

        private fun setGLColorFromInt(color: Int) {
            val red = (color shr 16 and 0xFF) / 255.0f
            val green = (color shr 8 and 0xFF) / 255.0f
            val blue = (color and 0xFF) / 255.0f
            val alpha = (color shr 24 and 0xFF) / 255f
            RenderSystem.color4f(red, green, blue, alpha)
        }

        private fun drawTextureWithMasking(matrix: Matrix4f, xCoord: Float, yCoord: Float, textureSprite: TextureAtlasSprite, maskTop: Int, maskRight: Int, zLevel: Float) {
            val uMin: Float = textureSprite.getMinU()
            var uMax: Float = textureSprite.getMaxU()
            val vMin: Float = textureSprite.getMinV()
            var vMax: Float = textureSprite.getMaxV()
            uMax = uMax - maskRight / 16f * (uMax - uMin)
            vMax = vMax - maskTop / 16f * (vMax - vMin)
            val tessellator: Tessellator = Tessellator.getInstance()
            val bufferBuilder: BufferBuilder = tessellator.getBuffer()
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
            bufferBuilder.pos(matrix, xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex()
            bufferBuilder.pos(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex()
            bufferBuilder.pos(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex()
            bufferBuilder.pos(matrix, xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex()
            tessellator.draw()
        }
    }
}