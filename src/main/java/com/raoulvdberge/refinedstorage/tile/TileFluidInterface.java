package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeFluidInterface;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileFluidInterface extends TileNode<NetworkNodeFluidInterface> {
    public static final TileDataParameter<FluidStack, TileFluidInterface> TANK_IN = new TileDataParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, null, t -> t.getNode().getTankIn().getFluid());
    public static final TileDataParameter<FluidStack, TileFluidInterface> TANK_OUT = new TileDataParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, null, t -> t.getNode().getTankOut().getFluid());

    public TileFluidInterface() {
        dataManager.addParameter(TANK_IN);
        dataManager.addParameter(TANK_OUT);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getNode().getTank());
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getIn());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    @Nonnull
    public NetworkNodeFluidInterface createNode(World world, BlockPos pos) {
        return new NetworkNodeFluidInterface(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeFluidInterface.ID;
    }
}
