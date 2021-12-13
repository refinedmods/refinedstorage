package com.refinedmods.refinedstorage.blockentity.craftingmonitor;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class CraftingMonitorBlockEntity extends NetworkNodeBlockEntity<CraftingMonitorNetworkNode> {
    public CraftingMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.CRAFTING_MONITOR, pos, state);

        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
    }    public static final BlockEntitySynchronizationParameter<Optional<UUID>, CraftingMonitorBlockEntity> TAB_SELECTED = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.OPTIONAL_UUID, Optional.empty(), t -> t.getNode().getTabSelected(), (t, v) -> {
        if (v.isPresent() && t.getNode().getTabSelected().isPresent() && v.get().equals(t.getNode().getTabSelected().get())) {
            t.getNode().setTabSelected(Optional.empty());
        } else {
            t.getNode().setTabSelected(v);
        }

        t.getNode().markDirty();
    });

    @Override
    @Nonnull
    public CraftingMonitorNetworkNode createNode(Level level, BlockPos pos) {
        return new CraftingMonitorNetworkNode(level, pos);
    }    public static final BlockEntitySynchronizationParameter<Integer, CraftingMonitorBlockEntity> TAB_PAGE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });




}
