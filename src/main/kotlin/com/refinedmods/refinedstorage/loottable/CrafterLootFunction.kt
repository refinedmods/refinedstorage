package com.refinedmods.refinedstorage.loottable

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.refinedmods.refinedstorage.RSLootFunctions
import com.refinedmods.refinedstorage.tile.CrafterTile
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType

class CrafterLootFunction(
        val conditions: Array<LootCondition>
) : LootFunction {
    override fun apply(stack: ItemStack, lootContext: LootContext): ItemStack {
        val tile: BlockEntity? = lootContext.get(LootContextParameters.BLOCK_ENTITY)
        var removedNode = (tile as CrafterTile).removedNode
        if (removedNode == null) {
            removedNode = tile.node
        }

        removedNode?.displayName?.let {
            stack.setCustomName(it)
        }

        return stack
    }

    override fun getType(): LootFunctionType {
        return RSLootFunctions.CRAFTER
    }

    // TODO See if necessary
//    class Serializer : LootFunction.Serializer<CrafterLootFunction?>() {
//        fun deserialize(`object`: JsonObject?, deserializationContext: JsonDeserializationContext?, conditions: Array<ILootCondition?>?): CrafterLootFunction {
//            return CrafterLootFunction(conditions)
//        }
//    }
}