package com.refinedmods.refinedstorage.apiimpl.network.node.storage

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.util.StackUtils.copy
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraftforge.fluids.FluidInstance


class FluidStorageWrapperStorageDisk(private val storage: FluidStorageNetworkNode, parent: IStorageDisk<FluidInstance>?) : IStorageDisk<FluidInstance?> {
    private val parent: IStorageDisk<FluidInstance>?
    override fun getPriority(): Int {
        return storage.priority
    }

    override fun getAccessType(): AccessType? {
        return parent!!.getAccessType()
    }

    override fun getStacks(): Collection<FluidInstance>? {
        return parent!!.getStacks()
    }

    @Nonnull
    override fun insert(@Nonnull stack: FluidInstance, size: Int, action: Action?): FluidInstance {
        return if (!IWhitelistBlacklist.acceptsFluid(storage.filters, storage.whitelistBlacklistMode, storage.compare, stack)) {
            copy(stack, size)
        } else parent!!.insert(stack, size, action)
    }

    @Nonnull
    override fun extract(@Nonnull stack: FluidInstance, size: Int, flags: Int, action: Action?): FluidInstance {
        return parent!!.extract(stack, size, flags, action)
    }

    override fun getStored(): Int {
        return parent!!.getStored()
    }

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, @Nullable remainder: FluidInstance): Int {
        return parent!!.getCacheDelta(storedPreInsertion, size, remainder)
    }

    override val capacity: Int
        get() = parent!!.capacity

    override fun setSettings(@Nullable listener: IStorageDiskListener?, context: IStorageDiskContainerContext) {
        parent!!.setSettings(listener, context)
    }

    override fun writeToNbt(): CompoundTag? {
        return parent!!.writeToNbt()
    }

    override val factoryId: Identifier
        get() = parent!!.factoryId

    init {
        this.parent = parent
        setSettings(null, storage)
    }
}