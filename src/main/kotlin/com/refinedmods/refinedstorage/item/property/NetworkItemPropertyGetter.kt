package com.refinedmods.refinedstorage.item.property

import com.refinedmods.refinedstorage.item.NetworkItem
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemStack

class NetworkItemPropertyGetter : IItemPropertyGetter {
    fun call(stack: ItemStack, @Nullable world: ClientWorld?, @Nullable entity: LivingEntity?): Float {
        return if (entity != null && NetworkItem.Companion.isValid(stack)) 1.0f else 0.0f
    }
}