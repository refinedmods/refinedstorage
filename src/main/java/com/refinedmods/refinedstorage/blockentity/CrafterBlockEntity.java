package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.screen.CrafterBlockEntitySynchronizationClientListener;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

public class CrafterBlockEntity extends NetworkNodeBlockEntity<CrafterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, CrafterBlockEntity> MODE =
        new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "crafter_mode"),
            EntityDataSerializers.INT, CrafterNetworkNode.CrafterMode.IGNORE.ordinal(),
            t -> t.getNode().getMode().ordinal(),
            (t, v) -> t.getNode().setMode(CrafterNetworkNode.CrafterMode.getById(v)));
    private static final BlockEntitySynchronizationParameter<Boolean, CrafterBlockEntity> HAS_ROOT =
        new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "crafter_has_root"),
            EntityDataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null,
            (t, v) -> new CrafterBlockEntitySynchronizationClientListener().onChanged(t, v));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(MODE)
        .addParameter(HAS_ROOT)
        .build();

    public CrafterBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.CRAFTER.get(), pos, state, SPEC, CrafterNetworkNode.class);
    }

    @Override
    @Nonnull
    public CrafterNetworkNode createNode(Level level, BlockPos pos) {
        return new CrafterNetworkNode(level, pos);
    }

    public IItemHandler getPatterns(Direction direction) {
        if (!direction.equals(this.getNode().getDirection())) {
            return getNode().getPatternInventory();
        }
        return null;
    }
}
