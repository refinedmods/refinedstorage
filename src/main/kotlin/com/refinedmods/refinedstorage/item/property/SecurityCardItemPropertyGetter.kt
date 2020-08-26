package com.refinedmods.refinedstorage.item.property

import com.refinedmods.refinedstorage.item.SecurityCardItem
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemStack

class SecurityCardItemPropertyGetter : IItemPropertyGetter {
    fun call(stack: ItemStack, @Nullable world: ClientWorld?, @Nullable entity: LivingEntity?): Float {
        return if (entity != null && SecurityCardItem.Companion.isValid(stack)) 1.0f else 0.0f
    }
}