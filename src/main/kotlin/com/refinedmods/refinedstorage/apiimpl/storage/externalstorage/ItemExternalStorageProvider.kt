package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.storage.IStorageProvider
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageContext
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorageProvider
import com.refinedmods.refinedstorage.tile.InterfaceTile
import com.refinedmods.refinedstorage.util.NetworkUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.BlockEntity
import net.minecraft.util.Directionimport
import java.util.function.Supplier

class ItemExternalStorageProvider : IExternalStorageProvider<ItemStack?> {
    fun canProvide(tile: BlockEntity?, direction: Direction?): Boolean {
        val node: INetworkNode = NetworkUtils.getNodeFromTile(tile)
        return if (node is IStorageProvider) {
            false
        } else WorldUtils.getItemHandler(tile, direction.getOpposite()) != null
    }

    @Nonnull
    fun provide(context: IExternalStorageContext?, tile: BlockEntity?, direction: Direction?): IExternalStorage<ItemStack>? {
        return ItemExternalStorage(context, label@ Supplier<IItemHandler> {
            if (!tile.getWorld().isBlockPresent(tile.getPos())) {
                return@label null
            }
            WorldUtils.getItemHandler(tile, direction.getOpposite())
        }, tile is InterfaceTile)
    }

    val priority: Int
        get() = 0
}