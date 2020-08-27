package com.refinedmods.refinedstorage.itemgroup

import com.refinedmods.refinedstorage.RS
import com.thinkslynk.fabric.annotations.registry.RegisterItemGroup
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

@RegisterItemGroup
class RSItemGroup: ItemGroup(GROUPS.size - 1, RS.ID) {
    override fun createIcon(): ItemStack {
        return BlockItemRegistryGenerated.CONSTRUCTOR_BLOCK.stackForRender
    }
}