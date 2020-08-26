package com.refinedmods.refinedstorage.loottable

import com.refinedmods.refinedstorage.RSLootFunctions
import com.refinedmods.refinedstorage.tile.ControllerTile
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType

class ControllerLootFunction(val conditions: Array<LootCondition>) : LootFunction {
    override fun apply(itemStack: ItemStack, lootContext: LootContext): ItemStack {
        val tile: BlockEntity? = lootContext.get(LootContextParameters.BLOCK_ENTITY)
        if (tile is ControllerTile) {
            val network = if (tile.removedNetwork == null) tile.network else tile.removedNetwork
            // TODO Energy
//            itemStack.getCapability(CapabilityEnergy.ENERGY).ifPresent({ energy -> energy.receiveEnergy(network.energyStorage.getEnergyStored(), false) })
        }
        return itemStack
    }

    override fun getType(): LootFunctionType {
        return RSLootFunctions.CONTROLLER
    }

    // TODO See if necessary
//    class Serializer : LootFunction.Serializer<ControllerLootFunction?>() {
//        fun deserialize(`object`: JsonObject?, deserializationContext: JsonDeserializationContext?, conditions: Array<ILootCondition?>?): ControllerLootFunction {
//            return ControllerLootFunction(conditions)
//        }
//    }
}