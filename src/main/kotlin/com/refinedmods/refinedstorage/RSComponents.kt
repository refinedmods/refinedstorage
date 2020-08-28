package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.api.component.INetworkNodeProxyComponent
import com.refinedmods.refinedstorage.api.component.NetworkNodeProxyComponent
import com.refinedmods.refinedstorage.block.CableBlock
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import nerdhub.cardinal.components.api.ComponentRegistry
import nerdhub.cardinal.components.api.ComponentType
import net.minecraft.block.BlockState
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import org.jetbrains.annotations.Nullable


@Suppress("UnstableApiUsage")
class RSComponents: BlockComponentInitializer {
    companion object {
        val NETWORK_NODE_PROXY: ComponentType<INetworkNodeProxyComponent> =
                ComponentRegistry.INSTANCE.registerStatic(
                        Identifier(RS.ID, INetworkNodeProxyComponent.ID),
                        INetworkNodeProxyComponent::class.java
                )
    }

    override fun registerBlockComponentFactories(registry: BlockComponentFactoryRegistry) {
        registry.registerFor(
                Identifier(RS.ID, CableBlock.ID),
                NETWORK_NODE_PROXY
        ) { _: BlockState, _: BlockView, _: BlockPos, _: Direction? ->
            NetworkNodeProxyComponent()
        }
    }
}