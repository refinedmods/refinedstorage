package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.ListTag
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.data.IModelData
import net.minecraftforge.client.model.data.ModelDataMap
import net.minecraftforge.client.model.data.ModelProperty
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

class DiskManipulatorTile : NetworkNodeTile<DiskManipulatorNetworkNode?>(RSTiles.DISK_MANIPULATOR) {
    private val diskCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.disks })
    private val diskState = arrayOfNulls<DiskState>(6)
    override fun writeUpdate(tag: CompoundTag): CompoundTag {
        super.writeUpdate(tag)
        val list = ListTag()
        for (state in getNode()!!.diskState) {
            list.add(IntNBT.valueOf(state!!.ordinal))
        }
        tag.put(NBT_DISK_STATE, list)
        return tag
    }

    override fun readUpdate(tag: CompoundTag?) {
        super.readUpdate(tag)
        val list = tag!!.getList(NBT_DISK_STATE, Constants.NBT.TAG_INT)
        for (i in list.indices) {
            diskState[i] = DiskState.values()[list.getInt(i)]
        }
        requestModelDataUpdate()
        WorldUtils.updateBlock(world, pos)
    }

    @get:Nonnull
    val modelData: IModelData
        get() = Builder().withInitial(DISK_STATE_PROPERTY, diskState).build()

    @Nonnull
    override fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            diskCapability.cast()
        } else super.getCapability<T>(cap, direction)
    }

    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): DiskManipulatorNetworkNode {
        return DiskManipulatorNetworkNode(world!!, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, DiskManipulatorTile> = IComparable.Companion.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, DiskManipulatorTile> = IWhitelistBlacklist.Companion.createParameter()
        val TYPE: TileDataParameter<Int, DiskManipulatorTile> = IType.Companion.createParameter()
        val IO_MODE = TileDataParameter(DataSerializers.VARINT, DiskManipulatorNetworkNode.IO_MODE_INSERT, Function { t: DiskManipulatorTile -> t.getNode()!!.ioMode }, BiConsumer<DiskManipulatorTile, Int> { t: DiskManipulatorTile, v: Int? ->
            t.getNode()!!.ioMode = v!!
            t.getNode()!!.markDirty()
        })
        val DISK_STATE_PROPERTY: ModelProperty<Array<DiskState>> = ModelProperty()
        private const val NBT_DISK_STATE = "DiskStates"
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(IO_MODE)
        Arrays.fill(diskState, DiskState.NONE)
    }
}