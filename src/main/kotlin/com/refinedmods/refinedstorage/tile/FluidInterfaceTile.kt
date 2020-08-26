package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import java.util.function.Function

class FluidInterfaceTile : NetworkNodeTile<FluidInterfaceNetworkNode?>(RSTiles.FLUID_INTERFACE) {
    private val tankCapability: LazyOptional<IFluidHandler> = LazyOptional.of({ getNode()!!.tank })
    private val inCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.`in` })

    @Nonnull
    override fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inCapability.cast()
        } else if (cap === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankCapability.cast()
        }
        return super.getCapability<T>(cap, direction)
    }

    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): FluidInterfaceNetworkNode {
        return FluidInterfaceNetworkNode(world, pos)
    }

    companion object {
        val TANK_IN: TileDataParameter<FluidInstance, FluidInterfaceTile> = TileDataParameter<T, E>(RSSerializers.FLUID_STACK_SERIALIZER, FluidInstance.EMPTY, Function<E, T> { t: E -> t.getNode().getTankIn().getFluid() })
        val TANK_OUT: TileDataParameter<FluidInstance, FluidInterfaceTile> = TileDataParameter<T, E>(RSSerializers.FLUID_STACK_SERIALIZER, FluidInstance.EMPTY, Function<E, T> { t: E -> t.getNode().getTankOut().getFluid() })
    }

    init {
        dataManager.addParameter(TANK_IN)
        dataManager.addParameter(TANK_OUT)
    }
}