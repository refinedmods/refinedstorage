package com.refinedmods.refinedstorage.util

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Material
import net.minecraft.sound.BlockSoundGroup

object BlockUtils {
    val DEFAULT_ROCK_PROPERTIES: AbstractBlock.Settings = FabricBlockSettings
            .of(Material.STONE)
            .hardness(1.9f)
            .sounds(BlockSoundGroup.STONE)
    val DEFAULT_GLASS_PROPERTIES: AbstractBlock.Settings = FabricBlockSettings
            .of(Material.GLASS)
            .hardness(0.35f)
            .sounds(BlockSoundGroup.GLASS)
}