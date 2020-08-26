package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive.DiskDriveNetworkNode
import com.refinedmods.refinedstorage.tile.config.*
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntNBT
import net.minecraft.nbt.ListTag
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
import java.util.function.Function

class DiskDriveTile : NetworkNodeTile<DiskDriveNetworkNode?>(RSTiles.DISK_DRIVE) {
    private val diskCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.getDisks() })
    private val diskState = arrayOfNulls<DiskState>(8)
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
    override fun createNode(world: World?, pos: BlockPos?): DiskDriveNetworkNode {
        return DiskDriveNetworkNode(world!!, pos)
    }

    companion object {
        val PRIORITY: TileDataParameter<Int, DiskDriveTile> = IPrioritizable.Companion.createParameter()
        val COMPARE: TileDataParameter<Int, DiskDriveTile> = IComparable.Companion.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, DiskDriveTile> = IWhitelistBlacklist.Companion.createParameter()
        val TYPE: TileDataParameter<Int, DiskDriveTile> = IType.Companion.createParameter()
        val ACCESS_TYPE: TileDataParameter<AccessType?, DiskDriveTile> = IAccessType.Companion.createParameter()
        val STORED: TileDataParameter<Long, DiskDriveTile> = TileDataParameter<T, E>(RSSerializers.LONG_SERIALIZER, 0L, Function<E, T> { t: E ->
            var stored: Long = 0
            for (storage in t.getNode().getItemDisks()) {
                if (storage != null) {
                    stored += storage.getStored().toLong()
                }
            }
            for (storage in t.getNode().getFluidDisks()) {
                if (storage != null) {
                    stored += storage.getStored().toLong()
                }
            }
            stored
        })
        val CAPACITY: TileDataParameter<Long, DiskDriveTile> = TileDataParameter<T, E>(RSSerializers.LONG_SERIALIZER, 0L, label@ Function<E, T> { t: E ->
            var capacity: Long = 0
            for (storage in t.getNode().getItemDisks()) {
                if (storage != null) {
                    if (storage.capacity == -1) {
                        return@label -1L
                    }
                    capacity += storage.capacity.toLong()
                }
            }
            for (storage in t.getNode().getFluidDisks()) {
                if (storage != null) {
                    if (storage.capacity == -1) {
                        return@label -1L
                    }
                    capacity += storage.capacity.toLong()
                }
            }
            capacity
        })
        val DISK_STATE_PROPERTY: ModelProperty<Array<DiskState>> = ModelProperty()
        private const val NBT_DISK_STATE = "DiskStates"
    }

    init {
        dataManager.addWatchedParameter(PRIORITY)
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(ACCESS_TYPE)
        dataManager.addWatchedParameter(STORED)
        dataManager.addWatchedParameter(CAPACITY)
        Arrays.fill(diskState, DiskState.NONE)
    }
}