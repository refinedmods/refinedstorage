package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.apiimpl.API
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*

class RootNetworkNode(override val network: INetwork, override val world: World, override val pos: BlockPos) : INetworkNode, INetworkNodeVisitor {
    override val id: Identifier?
        get() = null

    override var owner: UUID?
        get() = null
        set(_) {}
    override val energyUsage: Int
        get() = 0

    override val itemStack: ItemStack
        get() {
            val state: BlockState = world.getBlockState(pos)
            val item: Item = BlockItem.BLOCK_ITEMS[state.block]!! // TODO Unsafe and probably not the correct method
            return ItemStack(item, 1)
        }

    override fun onConnected(network: INetwork?) {}
    override fun onDisconnected(network: INetwork?) {}
    override val isActive: Boolean
        get() = false

    override fun update() {}
    override fun write(tag: CompoundTag): CompoundTag {
        return tag
    }

    override fun markDirty() {}
    override fun visit(operator: INetworkNodeVisitor.Operator?) {
        for (facing in Direction.values()) {
            operator!!.apply(world, pos.offset(facing), facing.opposite)
        }
    }

    override fun equals(other: Any?): Boolean {
        return API.instance().isNetworkNodeEqual(this, other)
    }

    override fun hashCode(): Int {
        return API.instance().getNetworkNodeHashCode(this)
    }

}