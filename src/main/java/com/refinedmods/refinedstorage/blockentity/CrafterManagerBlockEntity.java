package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.CrafterManagerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterManagerBlockEntity extends NetworkNodeBlockEntity<CrafterManagerNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, CrafterManagerBlockEntity> SEARCH_BOX_MODE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(CrafterManagerScreen.class, crafterManager -> crafterManager.getSearchField().setMode(p)));
    public static final BlockEntitySynchronizationParameter<Integer, CrafterManagerBlockEntity> SIZE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, IGrid.SIZE_STRETCH, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(CrafterManagerScreen.class, BaseScreen::init));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(SIZE)
        .addWatchedParameter(SEARCH_BOX_MODE)
        .build();

    public CrafterManagerBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.CRAFTER_MANAGER.get(), pos, state, SPEC, CrafterManagerNetworkNode.class);
    }

    @Override
    public CrafterManagerNetworkNode createNode(Level level, BlockPos pos) {
        return new CrafterManagerNetworkNode(level, pos);
    }
}
