package com.refinedmods.refinedstorage.render.model

import com.google.common.collect.ImmutableList
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.BakedQuad
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.util.Direction
import net.minecraft.util.math.vector.TransformationMatrix
import net.minecraft.util.math.vector.Vector3f
import net.minecraftforge.client.model.data.EmptyModelData
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder
import net.minecraftforge.client.model.pipeline.TRSRTransformer
import net.minecraftforge.common.model.TransformationHelper
import java.util.*

object QuadTransformer {
    fun getTransformedQuads(model: IBakedModel, facing: Direction, @Nullable translation: Vector3f?, state: BlockState?, rand: Random?, side: Direction?): List<BakedQuad> {
        var side: Direction? = side
        val r: Double = Math.PI * (360 - facing.getOpposite().getHorizontalIndex() * 90) / 180.0
        val transformation = TransformationMatrix(translation, TransformationHelper.quatFromXYZ(Vector3f(0, r.toFloat(), 0), false), null, null)
        val quads: ImmutableList.Builder<BakedQuad> = ImmutableList.builder<BakedQuad>()
        if (side != null && side.getHorizontalIndex() > -1) {
            val faceOffset: Int = 4 + Direction.NORTH.getHorizontalIndex() - facing.getHorizontalIndex()
            side = Direction.byHorizontalIndex((side.getHorizontalIndex() + faceOffset) % 4)
        }
        for (quad in model.getQuads(state, side, rand, EmptyModelData.INSTANCE)) {
            val builder = BakedQuadBuilder(quad.func_187508_a())
            val transformer = TRSRTransformer(builder, transformation.blockCenterToCorner())
            quad.pipe(transformer)
            quads.add(builder.build())
        }
        return quads.build()
    }
}