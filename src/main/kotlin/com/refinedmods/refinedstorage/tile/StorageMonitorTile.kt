package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidInstance

class StorageMonitorTile : NetworkNodeTile<StorageMonitorNetworkNode?>(RSTiles.STORAGE_MONITOR) {
    var stackType = 0
        private set
    var amount = 0
        private set

    @get:Nullable
    @Nullable
    var itemStack = ItemStack.EMPTY
        private set

    @Nullable
    private var fluidStack: FluidInstance = FluidInstance.EMPTY
    override fun createNode(world: World?, pos: BlockPos?): StorageMonitorNetworkNode {
        return StorageMonitorNetworkNode(world, pos)
    }

    override fun writeUpdate(tag: CompoundTag): CompoundTag {
        super.writeUpdate(tag)
        val stack: ItemStack = getNode()!!.itemFilters.getStackInSlot(0)
        if (!stack.isEmpty) {
            tag.put(NBT_STACK, stack.write(CompoundTag()))
        }
        val fluid: FluidInstance = getNode()!!.fluidFilters.getFluid(0)
        if (!fluid.isEmpty()) {
            tag.put(NBT_FLUIDSTACK, fluid.writeToNBT(CompoundTag()))
        }
        tag.putInt(NBT_TYPE, getNode()!!.getType())
        tag.putInt(NBT_AMOUNT, getNode()!!.amount)
        return tag
    }

    override fun readUpdate(tag: CompoundTag?) {
        super.readUpdate(tag)
        fluidStack = if (tag!!.contains(NBT_FLUIDSTACK)) FluidInstance.loadFluidInstanceFromNBT(tag.getCompound(NBT_FLUIDSTACK)) else FluidInstance.EMPTY
        itemStack = if (tag.contains(NBT_STACK)) ItemStack.read(tag.getCompound(NBT_STACK)) else ItemStack.EMPTY
        stackType = if (tag.contains(NBT_TYPE)) tag.getInt(NBT_TYPE) else IType.Companion.ITEMS
        amount = tag.getInt(NBT_AMOUNT)
    }

    @Nullable
    fun getFluidInstance(): FluidInstance {
        return fluidStack
    }

    companion object {
        val COMPARE: TileDataParameter<Int, StorageMonitorTile> = IComparable.Companion.createParameter()
        val TYPE: TileDataParameter<Int, StorageMonitorTile> = IType.Companion.createParameter()
        private const val NBT_TYPE = "Type"
        private const val NBT_FLUIDSTACK = "FluidInstance"
        private const val NBT_STACK = "Stack"
        private const val NBT_AMOUNT = "Amount"
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(TYPE)
    }
}