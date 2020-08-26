package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import reborncore.common.fluid.container.FluidInstance

class FluidStorageDiskFactory : IStorageDiskFactory<FluidInstance> {

    override fun createFromNbt(world: ServerWorld, tag: CompoundTag): IStorageDisk<FluidInstance> {
        val disk = FluidStorageDisk(world, tag.getInt(FluidStorageDisk.NBT_CAPACITY))
        val list = tag.getList(FluidStorageDisk.NBT_FLUIDS, ListTag().type.toInt())
        for (i in list.indices) {
            val stack = FluidInstance()
            stack.read(list.getCompound(i))
            if (!stack.isEmpty) {
                disk.rawStacks.put(stack.fluid, stack)
            }
        }
        return disk
    }

    override fun create(world: ServerWorld, capacity: Int): IStorageDisk<FluidInstance> {
        return FluidStorageDisk(world, capacity)
    }

    companion object {
        @JvmField
        val ID = Identifier(RS.ID, "fluid")
    }
}