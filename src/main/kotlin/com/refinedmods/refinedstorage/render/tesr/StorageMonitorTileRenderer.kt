package com.refinedmods.refinedstorage.render.tesr

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import com.refinedmods.refinedstorage.RSBlocks
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.block.StorageMonitorBlock
import com.refinedmods.refinedstorage.tile.StorageMonitorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.tileentity.BlockEntityRenderer
import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher
import net.minecraft.fluid.Fluid
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraft.util.Identifier
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.common.model.TransformationHelper
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.FluidInstance

class StorageMonitorTileRenderer(dispatcher: BlockEntityRendererDispatcher?) : BlockEntityRenderer<StorageMonitorTile?>(dispatcher) {
    fun render(tile: StorageMonitorTile, partialTicks: Float, matrixStack: MatrixStack, renderTypeBuffer: IRenderTypeBuffer, i: Int, i1: Int) {
        var direction: Direction = Direction.NORTH
        val state = tile.world!!.getBlockState(tile.pos)
        if (state.block is StorageMonitorBlock) {
            direction = state.get(RSBlocks.STORAGE_MONITOR.direction.property)
        }
        val light: Int = WorldRenderer.getCombinedLight(tile.world, tile.pos.add(direction.getDirectionVec()))
        val rotation = (Math.PI * (360 - direction.getOpposite().getHorizontalIndex() * 90) / 180.0) as Float
        val type = tile.stackType
        val itemStack = tile.itemStack
        val fluidStack: FluidInstance? = tile.fluidStack
        if (type == IType.ITEMS && itemStack != null && !itemStack.isEmpty) {
            renderItem(matrixStack, renderTypeBuffer, direction, rotation, light, itemStack)
            val amount = instance().getQuantityFormatter()!!.formatWithUnits(tile.amount)
            renderText(matrixStack, renderTypeBuffer, direction, rotation, light, amount)
        } else if (type == IType.FLUIDS && fluidStack != null && !fluidStack.isEmpty()) {
            renderFluid(matrixStack, renderTypeBuffer, direction, rotation, light, fluidStack)
            val amount = instance().getQuantityFormatter()!!.formatInBucketFormWithOnlyTrailingDigitsIfZero(tile.amount)
            renderText(matrixStack, renderTypeBuffer, direction, rotation, light, amount)
        }
    }

    private fun renderText(matrixStack: MatrixStack, renderTypeBuffer: IRenderTypeBuffer, direction: Direction, rotation: Float, light: Int, amount: String?) {
        matrixStack.push()
        val stringOffset: Float = -(Minecraft.getInstance().fontRenderer.getStringWidth(amount) * 0.01f) / 2f
        matrixStack.translate(0.5, 0.5, 0.5)
        matrixStack.translate(
                direction.getXOffset() as Float * 0.5f + direction.getZOffset() * stringOffset,
                -0.275,
                direction.getZOffset() as Float * 0.5f - direction.getXOffset() * stringOffset
        )
        matrixStack.rotate(TransformationHelper.quatFromXYZ(Vector3f(direction.getXOffset() * 180, 0, direction.getZOffset() * 180), true))
        matrixStack.rotate(TransformationHelper.quatFromXYZ(Vector3f(0, rotation, 0), false))
        matrixStack.scale(0.01f, 0.01f, 0.01f)
        Minecraft.getInstance().fontRenderer.renderString(
                amount,
                0,
                0,
                -1,
                false,
                matrixStack.getLast().getMatrix(),
                renderTypeBuffer,
                false,
                0,
                light
        )
        matrixStack.pop()
    }

    private fun renderItem(matrixStack: MatrixStack, renderTypeBuffer: IRenderTypeBuffer, direction: Direction, rotation: Float, light: Int, itemStack: ItemStack) {
        matrixStack.push()
        matrixStack.translate(0.5, 0.5, 0.5)
        matrixStack.translate(direction.getXOffset() as Float * 0.5f, 0, direction.getZOffset() as Float * 0.5f)
        matrixStack.rotate(TransformationHelper.quatFromXYZ(Vector3f(0, rotation, 0), false))
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        Minecraft.getInstance().getItemRenderer().renderItem(
                itemStack,
                ItemCameraTransforms.TransformType.FIXED,
                light,
                OverlayTexture.NO_OVERLAY,
                matrixStack,
                renderTypeBuffer
        )
        matrixStack.pop()
    }

    private fun renderFluid(matrixStack: MatrixStack, renderTypeBuffer: IRenderTypeBuffer, direction: Direction, rotation: Float, light: Int, fluidStack: FluidInstance) {
        matrixStack.push()
        matrixStack.translate(0.5, 0.5, 0.5)
        matrixStack.translate(direction.getXOffset() as Float * 0.51f, 0.5f, direction.getZOffset() as Float * 0.51f)
        matrixStack.rotate(TransformationHelper.quatFromXYZ(Vector3f(0, rotation, 0), false))
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        val fluid: Fluid = fluidStack.getFluid()
        val attributes: FluidAttributes = fluid.getAttributes()
        val fluidStill: Identifier = attributes.getStillTexture(fluidStack)
        val sprite: TextureAtlasSprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill)
        val fluidColor: Int = attributes.getColor(fluidStack)
        val buffer: IVertexBuilder = renderTypeBuffer.getBuffer(RenderType.getText(sprite.getAtlasTexture().getTextureLocation()))
        val colorRed = fluidColor shr 16 and 0xFF
        val colorGreen = fluidColor shr 8 and 0xFF
        val colorBlue = fluidColor and 0xFF
        val colorAlpha = fluidColor shr 24 and 0xFF
        buffer.pos(matrixStack.getLast().getMatrix(), -0.5f, -0.5f, 0f)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMinU(), sprite.getMinV())
                .lightmap(light)
                .endVertex()
        buffer.pos(matrixStack.getLast().getMatrix(), 0.5f, -0.5f, 0f)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMaxU(), sprite.getMinV())
                .lightmap(light)
                .endVertex()
        buffer.pos(matrixStack.getLast().getMatrix(), 0.5f, -1.5f, 0f)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMaxU(), sprite.getMaxV())
                .lightmap(light)
                .endVertex()
        buffer.pos(matrixStack.getLast().getMatrix(), -0.5f, -1.5f, 0f)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .tex(sprite.getMinU(), sprite.getMaxV())
                .lightmap(light)
                .endVertex()
        matrixStack.pop()
    }
}