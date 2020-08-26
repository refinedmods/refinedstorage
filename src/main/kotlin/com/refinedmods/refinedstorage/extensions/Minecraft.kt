package com.refinedmods.refinedstorage.extensions

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun ItemStack.safeAdd(size: Int) {
    this.count = (count.toLong() + size.toLong())
            .coerceAtMost(maxCount.toLong())
            .coerceAtLeast(0)
            .toInt()
}

fun ItemStack.safeSubtract(size: Int) {
    this.safeAdd(0 - size)
}

fun Inventory.getStacks(): Collection<ItemStack> =
        (0..this.size()).map { this.getStack(it) }

fun Inventory.drop(world: World, pos: BlockPos) {
    // TODO figure out how to drop an inventory at a position...
}

val LIST_TAG_TYPE by lazy { ListTag().type.toInt() }
