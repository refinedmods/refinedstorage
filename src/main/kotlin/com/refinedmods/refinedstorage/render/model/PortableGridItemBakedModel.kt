package com.refinedmods.refinedstorage.render.model

import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.Direction
import java.util.*

class PortableGridItemBakedModel(base: IBakedModel, @Nullable disk: IBakedModel?) : IBakedModel {
    private val base: IBakedModel

    @Nullable
    private val disk: IBakedModel?
    val overrides: ItemOverrideList
        get() = ItemOverrideList.EMPTY
    val itemCameraTransforms: ItemCameraTransforms
        get() = base.getItemCameraTransforms()

    @Nonnull
    fun getQuads(@Nullable state: BlockState?, @Nullable side: Direction?, @Nonnull rand: Random?): List<BakedQuad> {
        val quads: MutableList<BakedQuad> = ArrayList<Any?>(base.getQuads(state, side, rand))
        if (disk != null) {
            quads.addAll(disk.getQuads(state, side, rand))
        }
        return quads
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

    init {
        this.base = base
        this.disk = disk
    }
}