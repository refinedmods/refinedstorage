package com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraftforge.items.ItemHandlerHelper


class ItemDriveWrapperStorageDisk(private val diskDrive: DiskDriveNetworkNode, private val parent: IStorageDisk<ItemStack>?) : IStorageDisk<ItemStack?> {
    private var lastState: DiskState
    override fun getPriority(): Int {
        return diskDrive.priority
    }

    override fun getAccessType(): AccessType? {
        return parent!!.getAccessType()
    }

    override fun getStacks(): Collection<ItemStack>? {
        return parent!!.getStacks()
    }

    @Nonnull
    override fun insert(@Nonnull stack: ItemStack, size: Int, action: Action?): ItemStack {
        return if (!IWhitelistBlacklist.acceptsItem(diskDrive.itemFilters, diskDrive.whitelistBlacklistMode, diskDrive.compare, stack)) {
            ItemHandlerHelper.copyStackWithSize(stack, size)
        } else parent!!.insert(stack, size, action)
    }

    @Nonnull
    override fun extract(@Nonnull stack: ItemStack, size: Int, flags: Int, action: Action?): ItemStack {
        return parent!!.extract(stack, size, flags, action)
    }

    override fun getStored(): Int {
        return parent!!.getStored()
    }

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, @Nullable remainder: ItemStack): Int {
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
        setSettings(
                IStorageDiskListener {
                    val currentState: DiskState = DiskState.Companion.get(getStored(), capacity)
                    if (lastState != currentState) {
                        lastState = currentState
                        diskDrive.requestBlockUpdate()
                    }
                },
                diskDrive
        )
        lastState = DiskState.Companion.get(getStored(), capacity)
    }
}