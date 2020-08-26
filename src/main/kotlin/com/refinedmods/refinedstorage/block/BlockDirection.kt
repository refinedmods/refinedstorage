package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.util.DirectionUtils
import net.minecraft.entity.LivingEntity
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

enum class BlockDirection(vararg allowed: Direction) {
    NONE,
    ANY(*Direction.values()),
    ANY_FACE_PLAYER(*Direction.values()),
    HORIZONTAL(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    val property: DirectionProperty

    fun getFrom(facing: Direction, pos: BlockPos, entity: LivingEntity): Direction {
        return when (this) {
            ANY -> facing.opposite
            ANY_FACE_PLAYER -> DirectionUtils.getFacingFromEntity(pos, entity)
            HORIZONTAL -> entity.horizontalFacing.opposite
            else -> throw RuntimeException("Unknown direction type")
        }
    }

    fun cycle(previous: Direction): Direction {
        return when (this) {
            ANY, ANY_FACE_PLAYER -> if (previous.ordinal + 1 >= Direction.values().size) {
                Direction.values()[0]
            } else {
                Direction.values()[previous.ordinal + 1]
            }
            HORIZONTAL -> previous.rotateYCounterclockwise()
            else -> throw RuntimeException("Unknown direction type")
        }
    }

    init {
        property = DirectionProperty.of("direction", allowed.asList())
    }
}