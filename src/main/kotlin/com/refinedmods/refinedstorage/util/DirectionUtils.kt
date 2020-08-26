package com.refinedmods.refinedstorage.util

import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

object DirectionUtils {
    fun getFacingFromEntity(clickedBlock: BlockPos, entity: LivingEntity): Direction {
        return Direction.getFacing(
                (entity.x - clickedBlock.x).toFloat(),
                (entity.y - clickedBlock.y).toFloat(),
                (entity.z - clickedBlock.z).toFloat()
        )
    }
}