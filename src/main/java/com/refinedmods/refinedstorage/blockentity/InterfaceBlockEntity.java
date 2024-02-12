package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.InterfaceNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

public class InterfaceBlockEntity extends NetworkNodeBlockEntity<InterfaceNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, InterfaceBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(RS.ID, "interface_compare"));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(COMPARE)
        .build();

    public InterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.INTERFACE.get(), pos, state, SPEC, InterfaceNetworkNode.class);
    }

    @Override
    @Nonnull
    public InterfaceNetworkNode createNode(Level level, BlockPos pos) {
        return new InterfaceNetworkNode(level, pos);
    }
}
