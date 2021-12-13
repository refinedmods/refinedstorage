package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    private final LazyOptional<IFluidHandler> tankCapability = LazyOptional.of(() -> getNode().getTank());
    private final LazyOptional<IItemHandler> inCapability = LazyOptional.of(() -> getNode().getIn());

    public FluidInterfaceTile(BlockPos pos, BlockState state) {
        super(RSTiles.FLUID_INTERFACE, pos, state);

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
    public FluidInterfaceNetworkNode createNode(Level level, BlockPos pos) {
        return new FluidInterfaceNetworkNode(level, pos);
    }
}
