package com.refinedmods.refinedstorage.render.model

import com.mojang.blaze3d.matrix.MatrixStack
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.Direction
import java.util.*

open class DelegateBakedModel(base: IBakedModel) : IBakedModel {
    protected val base: IBakedModel
    open fun getQuads(@Nullable state: BlockState?, @Nullable side: Direction, rand: Random): List<BakedQuad?>? {
        return base.getQuads(state, side, rand)
    }

    val isAmbientOcclusion: Boolean
        get() = base.isAmbientOcclusion()
    val isGui3d: Boolean
        get() = base.isGui3d()

    fun func_230044_c_(): Boolean {
        return base.func_230044_c_()
    }

    val isBuiltInRenderer: Boolean
        get() = base.isBuiltInRenderer()
    val particleTexture: TextureAtlasSprite
        get() = base.getParticleTexture()
    open val overrides: ItemOverrideList
        get() = base.getOverrides()
    val itemCameraTransforms: ItemCameraTransforms
        get() = base.getItemCameraTransforms()

    fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType?, matrixStack: MatrixStack?): IBakedModel {
        return base.handlePerspective(cameraTransformType, matrixStack)
    }

    init {
        this.base = base
    }
}