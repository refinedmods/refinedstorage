package com.refinedmods.refinedstorage.apiimpl.storage.disk.factory

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk
import com.refinedmods.refinedstorage.util.StackUtils.deserializeStackFromNbt
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier

class ItemStorageDiskFactory : IStorageDiskFactory<ItemStack> {
    fun createFromNbt(world: ServerWorld, tag: CompoundTag): IStorageDisk<ItemStack> {
        val disk = ItemStorageDisk(world, tag.getInt(ItemStorageDisk.NBT_CAPACITY))
        val list: ListTag = tag.getList(ItemStorageDisk.NBT_ITEMS, ListTag().type.toInt())
        for (i in 0 until list.size) {
            val stack = deserializeStackFromNbt(list.getCompound(i))
            if (!stack.isEmpty) {
                disk.rawStacks.put(stack.item, stack)
            }
        }

        return disk
    }

    override fun create(world: ServerWorld, capacity: Int): IStorageDisk<ItemStack> {
        return ItemStorageDisk(world, capacity)
    }

    companion object {
        @JvmField
        val ID = Identifier(RS.ID, "item")
    }
}