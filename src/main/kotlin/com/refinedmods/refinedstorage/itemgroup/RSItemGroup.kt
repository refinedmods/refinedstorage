package com.refinedmods.refinedstorage.itemgroup

import com.refinedmods.refinedstorage.RS
import com.thinkslynk.fabric.annotations.registry.RegisterItemGroup
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

@RegisterItemGroup
class RSItemGroup: ItemGroup(GROUPS.size - 1, RS.ID) {
    init {
        (BUILDING_BLOCKS as ItemGroupExtensions).fabric_expandArray()
    }
    override fun createIcon(): ItemStack {
        return BlockItemRegistryGenerated.CREATIVE_CONTROLLER_BLOCK.stackForRender
    }
}