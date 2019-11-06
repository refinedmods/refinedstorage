package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidInterfaceTile extends NetworkNodeTile<FluidInterfaceNetworkNode> {
    public static final TileDataParameter<FluidStack, FluidInterfaceTile> TANK_IN = new TileDataParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, FluidStack.EMPTY, t -> t.getNode().getTankIn().getFluid());
    public static final TileDataParameter<FluidStack, FluidInterfaceTile> TANK_OUT = new TileDataParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, FluidStack.EMPTY, t -> t.getNode().getTankOut().getFluid());

    private LazyOptional<IFluidHandler> tankCapability = LazyOptional.of(() -> getNode().getTank());
    private LazyOptional<IItemHandler> inCapability = LazyOptional.of(() -> getNode().getIn());

    public FluidInterfaceTile() {
        super(RSTiles.FLUID_INTERFACE);

        dataManager.addParameter(TANK_IN);
        dataManager.addParameter(TANK_OUT);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inCapability.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankCapability.cast();
        }

        return super.getCapability(cap, direction);
    }

    @Override
    @Nonnull
    public FluidInterfaceNetworkNode createNode(World world, BlockPos pos) {
        return new FluidInterfaceNetworkNode(world, pos);
    }
}
