package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.state.StateManager
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

abstract class BaseBlock(settings: Settings): Block(settings) {
    open val direction: BlockDirection
        get() = BlockDirection.NONE

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        val dir = direction
        if (dir != BlockDirection.NONE) {
            val newDirection = dir.cycle(state.get(dir.property))
            return state.with(dir.property, newDirection)
        }
        return super.rotate(state, rotation)
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        super.onStateReplaced(state, world, pos, newState, moved)
        if (direction != BlockDirection.NONE &&
                state.block === newState.block &&
                state.get(direction.property) !== newState.get(direction.property)) {
            onDirectionChanged(world, pos, newState.get(direction.property))
        }
    }

    protected open fun onDirectionChanged(world: World, pos: BlockPos, newDirection: Direction) {}

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        if (this.direction != BlockDirection.NONE) {
            builder.add(direction.property)
        }
    }
}