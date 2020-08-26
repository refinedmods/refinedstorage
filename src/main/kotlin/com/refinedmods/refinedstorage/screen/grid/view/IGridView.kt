package com.refinedmods.refinedstorage.screen.grid.view

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.*

interface IGridView {
    fun getStacks(): List<IGridStack?>

    @Nullable
    operator fun get(id: UUID?): IGridStack?
    fun setStacks(stacks: List<IGridStack>)
    fun postChange(stack: IGridStack, delta: Int)
    fun setCanCraft(canCraft: Boolean)
    fun canCraft(): Boolean
    fun sort()
}