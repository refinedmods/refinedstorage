package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.block.BaseBlock
import com.refinedmods.refinedstorage.block.BlockDirection
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext

open class BaseBlockItem(private val block: BaseBlock, builder: Settings) : BlockItem(block, builder) {

    override fun place(context: ItemPlacementContext, state: BlockState): Boolean {
        val result: Boolean = super.place(context, state)
        if (result && block.direction !== BlockDirection.NONE) {
            val newState = state.with(
                    block.direction.property,
                    block.direction.getFrom(
                            context.side,
                            context.blockPos,
                            context.player!! // TODO This isn't safe!
                    )
            )

            context.world.setBlockState(context.blockPos, newState)
        }
        return result
    }

}