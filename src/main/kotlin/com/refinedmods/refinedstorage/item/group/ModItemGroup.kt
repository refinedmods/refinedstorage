package com.refinedmods.refinedstorage.item.group

import com.refinedmods.refinedstorage.RS
import com.thinkslynk.fabric.annotations.registry.RegisterItemGroup
import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

@RegisterItemGroup
fun curedStorage(): ItemGroup {
    return FabricItemGroupBuilder.create(Identifier(RS.ID))
            .icon { BlockItemRegistryGenerated.CREATIVE_CONTROLLER_BLOCK.stackForRender }
            .build()
}