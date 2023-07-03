package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class WirelessTransmitterBlockEntity extends NetworkNodeBlockEntity<WirelessTransmitterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, WirelessTransmitterBlockEntity> RANGE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "wireless_transmitter_range"), EntityDataSerializers.INT, 0, t -> t.getNode().getRange());

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(RANGE)
        .build();

    public WirelessTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.WIRELESS_TRANSMITTER.get(), pos, state, SPEC);
    }

    @Override
    @Nonnull
    public WirelessTransmitterNetworkNode createNode(Level level, BlockPos pos) {
        return new WirelessTransmitterNetworkNode(level, pos);
    }
}
