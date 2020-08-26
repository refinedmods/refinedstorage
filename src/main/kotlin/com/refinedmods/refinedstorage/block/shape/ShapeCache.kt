package com.refinedmods.refinedstorage.block.shape

import net.minecraft.block.BlockState
import net.minecraft.util.shape.VoxelShape
import java.util.*
import java.util.function.Function

object ShapeCache {
    private val CACHE: MutableMap<BlockState, VoxelShape> = HashMap()
    @JvmStatic
    fun getOrCreate(state: BlockState, shapeFactory: Function<BlockState, VoxelShape>): VoxelShape {
        return CACHE.computeIfAbsent(state, shapeFactory)
    }
}