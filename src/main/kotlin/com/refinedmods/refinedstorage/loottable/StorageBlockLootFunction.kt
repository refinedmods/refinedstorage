package com.refinedmods.refinedstorage.loottable

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.refinedmods.refinedstorage.RSLootFunctions
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode
import com.refinedmods.refinedstorage.tile.FluidStorageTile
import com.refinedmods.refinedstorage.tile.StorageTile
import net.fabricmc.fabric.api.loot.v1.LootJsonParser
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.nbt.CompoundTag

class StorageBlockLootFunction(
        val conditions: Array<LootCondition>
): LootFunction {
    override fun apply(stack: ItemStack, lootContext: LootContext): ItemStack {
        val tile: BlockEntity? = lootContext.get(LootContextParameters.BLOCK_ENTITY)

        // This code needs to work without the node being removed as well.
        // For example: the Destructor calls getDrops before the node has been removed.
        if (tile is StorageTile) {
            var removedNode = tile.removedNode
            if (removedNode == null) {
                removedNode = tile.node
            }

            val tag = CompoundTag()
            tag.putUuid(StorageNetworkNode.NBT_ID, removedNode!!.storageId)
            stack.tag = tag
        } else if (tile is FluidStorageTile) {
            var removedNode = tile.removedNode
            if (removedNode == null) {
                removedNode = tile.node
            }
            val tag = CompoundTag()
            tag.putUuid(FluidStorageNetworkNode.NBT_ID, removedNode!!.storageId)
            stack.tag = tag
        }
        return stack
    }

    override fun getType(): LootFunctionType {
        return RSLootFunctions.STORAGE_BLOCK
    }

    // TODO See if this is really needed...
//    class Serializer: LootJsonParser<StorageBlockLootFunction?>() {
//        fun deserialize(`object`: JsonObject?, deserializationContext: JsonDeserializationContext?, conditions: Array<LootCondition>): StorageBlockLootFunction {
//            return StorageBlockLootFunction(conditions)
//        }
//    }
}