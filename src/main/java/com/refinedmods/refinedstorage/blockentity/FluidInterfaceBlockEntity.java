package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.FluidInterfaceNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidInterfaceBlockEntity extends NetworkNodeBlockEntity<FluidInterfaceNetworkNode> {
    public static final BlockEntitySynchronizationParameter<FluidStack, FluidInterfaceBlockEntity> TANK_IN = new BlockEntitySynchronizationParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, FluidStack.EMPTY, t -> t.getNode().getTankIn().getFluid());
    public static final BlockEntitySynchronizationParameter<FluidStack, FluidInterfaceBlockEntity> TANK_OUT = new BlockEntitySynchronizationParameter<>(RSSerializers.FLUID_STACK_SERIALIZER, FluidStack.EMPTY, t -> t.getNode().getTankOut().getFluid());

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addParameter(TANK_IN)
        .addParameter(TANK_OUT)
        .build();

    private final LazyOptional<IFluidHandler> tankCapability = LazyOptional.of(() -> getNode().getTank());
    private final LazyOptional<IItemHandler> inCapability = LazyOptional.of(() -> getNode().getIn());

    public FluidInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.FLUID_INTERFACE.get(), pos, state, SPEC);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inCapability.cast();
        } else if (cap == ForgeCapabilities.FLUID_HANDLER) {
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
