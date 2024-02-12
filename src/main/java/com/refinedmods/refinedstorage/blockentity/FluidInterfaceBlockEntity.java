package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class FluidInterfaceBlockEntity extends NetworkNodeBlockEntity<FluidInterfaceNetworkNode> {
    public static final BlockEntitySynchronizationParameter<FluidStack, FluidInterfaceBlockEntity> TANK_IN = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "fluid_interface_in"), RSSerializers.FLUID_STACK_SERIALIZER, FluidStack.EMPTY, t -> t.getNode().getTankIn().getFluid());
    public static final BlockEntitySynchronizationParameter<FluidStack, FluidInterfaceBlockEntity> TANK_OUT = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "fluid_interface_out"), RSSerializers.FLUID_STACK_SERIALIZER, FluidStack.EMPTY, t -> t.getNode().getTankOut().getFluid());

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addParameter(TANK_IN)
            .addParameter(TANK_OUT)
            .build();

    public FluidInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.FLUID_INTERFACE.get(), pos, state, SPEC, FluidInterfaceNetworkNode.class);
    }

    @Override
    @Nonnull
    public FluidInterfaceNetworkNode createNode(Level level, BlockPos pos) {
        return new FluidInterfaceNetworkNode(level, pos);
    }
}
