package com.refinedmods.refinedstorage

import com.thinkslynk.fabric.generated.BlockItemRegistryGenerated
import com.thinkslynk.fabric.generated.BlockRegistryGenerated
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.block.Block
import net.minecraft.client.render.RenderLayer

class RSClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderLayer.getCutout(),
                BlockRegistryGenerated.CABLE_BLOCK,
                BlockRegistryGenerated.CONSTRUCTOR_BLOCK,
                BlockRegistryGenerated.CONTROLLER_BLOCK,
                BlockRegistryGenerated.CRAFTER_MANAGER_BLOCK,
                BlockRegistryGenerated.CRAFTER_BLOCK,
                BlockRegistryGenerated.CRAFTING_GRID_BLOCK,
                BlockRegistryGenerated.CRAFTING_MONITOR_BLOCK,
                BlockRegistryGenerated.DESTRUCTOR_BLOCK,
                BlockRegistryGenerated.DETECTOR_BLOCK,
                BlockRegistryGenerated.DISK_MANIPULATOR_BLOCK,
                BlockRegistryGenerated.FLUID_GRID_BLOCK,
                BlockRegistryGenerated.GRID_BLOCK,
                BlockRegistryGenerated.NETWORK_RECEIVER_BLOCK,
                BlockRegistryGenerated.NETWORK_TRANSMITTER_BLOCK,
                BlockRegistryGenerated.PATTERN_GRID_BLOCK,
                BlockRegistryGenerated.RELAY_BLOCK,
                BlockRegistryGenerated.SECURITY_MANAGER_BLOCK,
                BlockRegistryGenerated.WIRELESS_TRANSMITTER_BLOCK
        )

        BlockRenderLayerMap.INSTANCE.putItems(
                RenderLayer.getCutout(),
                BlockItemRegistryGenerated.CONSTRUCTOR_BLOCK,
                BlockItemRegistryGenerated.DESTRUCTOR_BLOCK
        )
    }

}