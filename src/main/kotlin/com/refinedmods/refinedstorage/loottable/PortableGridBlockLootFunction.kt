package com.refinedmods.refinedstorage.loottable

import com.refinedmods.refinedstorage.RSLootFunctions
import com.refinedmods.refinedstorage.tile.grid.portable.PortableGridTile
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType

class PortableGridBlockLootFunction(
        val conditions: Array<LootCondition>
) : LootFunction {
    override fun apply(stack: ItemStack, lootContext: LootContext): ItemStack {
        val tile: BlockEntity? = lootContext.get(LootContextParameters.BLOCK_ENTITY)
        if (tile is PortableGridTile) {
            tile.applyDataFromTileToItem(stack)
        }
        return stack
    }

    override fun getType(): LootFunctionType {
        return RSLootFunctions.PORTABLE_GRID
    }

    // TODO See if necessary
//    class Serializer : LootFunction.Serializer<PortableGridBlockLootFunction?>() {
//        fun deserialize(`object`: JsonObject?, deserializationContext: JsonDeserializationContext?, conditions: Array<ILootCondition?>?): PortableGridBlockLootFunction {
//            return PortableGridBlockLootFunction(conditions)
//        }
//    }
}