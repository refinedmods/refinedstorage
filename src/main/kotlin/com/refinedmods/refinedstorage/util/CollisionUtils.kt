package com.refinedmods.refinedstorage.util

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.math.vector.Vector3d

object CollisionUtils {
    fun isInBounds(shape: VoxelShape, pos: BlockPos?, hit: Vector3d): Boolean {
        val aabb: AxisAlignedBB = shape.getBoundingBox().offset(pos)
        return hit.x >= aabb.minX && hit.x <= aabb.maxX && hit.y >= aabb.minY && hit.y <= aabb.maxY && hit.z >= aabb.minZ && hit.z <= aabb.maxZ
    }
}