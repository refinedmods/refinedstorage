package com.refinedmods.refinedstorage.blockentity.craftingmonitor;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class CraftingMonitorBlockEntity extends NetworkNodeBlockEntity<CraftingMonitorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Optional<UUID>, CraftingMonitorBlockEntity> TAB_SELECTED = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "crafting_monitor_tab_selected"), EntityDataSerializers.OPTIONAL_UUID, Optional.empty(), t -> t.getNode().getTabSelected(), (t, v) -> {
        if (v.isPresent() && t.getNode().getTabSelected().isPresent() && v.get().equals(t.getNode().getTabSelected().get())) {
            t.getNode().setTabSelected(Optional.empty());
        } else {
            t.getNode().setTabSelected(v);
        }

        t.getNode().markDirty();
    });

    public static final BlockEntitySynchronizationParameter<Integer, CraftingMonitorBlockEntity> TAB_PAGE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "crafting_monitor_tab_page"), EntityDataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(TAB_SELECTED)
        .addWatchedParameter(TAB_PAGE)
        .build();

    public CraftingMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.CRAFTING_MONITOR.get(), pos, state, SPEC);
    }

    @Override
    @Nonnull
    public CraftingMonitorNetworkNode createNode(Level level, BlockPos pos) {
        return new CraftingMonitorNetworkNode(level, pos);
    }
}
