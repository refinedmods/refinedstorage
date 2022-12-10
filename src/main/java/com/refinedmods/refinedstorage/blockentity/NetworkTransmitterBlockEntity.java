package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkTransmitterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class NetworkTransmitterBlockEntity extends NetworkNodeBlockEntity<NetworkTransmitterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, NetworkTransmitterBlockEntity> DISTANCE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getDistance());
    public static final BlockEntitySynchronizationParameter<Optional<ResourceLocation>, NetworkTransmitterBlockEntity> RECEIVER_DIMENSION = new BlockEntitySynchronizationParameter<>(RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty(), t -> {
        if (t.getNode().getReceiverDimension() != null) {
            return Optional.of(t.getNode().getReceiverDimension().location());
        }

        return Optional.empty();
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(DISTANCE)
        .addWatchedParameter(RECEIVER_DIMENSION)
        .build();

    private final LazyOptional<IItemHandler> networkCardCapability = LazyOptional.of(() -> getNode().getNetworkCard());

    public NetworkTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.NETWORK_TRANSMITTER.get(), pos, state, SPEC);
    }

    @Override
    @Nonnull
    public NetworkTransmitterNetworkNode createNode(Level level, BlockPos pos) {
        return new NetworkTransmitterNetworkNode(level, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return networkCardCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
