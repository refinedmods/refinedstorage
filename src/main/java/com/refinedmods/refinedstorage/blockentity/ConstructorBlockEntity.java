package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import javax.annotation.Nonnull;

public class ConstructorBlockEntity extends NetworkNodeBlockEntity<ConstructorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, ConstructorBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(RS.ID, "constructor_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, ConstructorBlockEntity> TYPE = IType.createParameter(new ResourceLocation(RS.ID, "constructor_type"));
    public static final BlockEntitySynchronizationParameter<Boolean, ConstructorBlockEntity> DROP = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "constructor_drop"), EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    public static final BlockEntitySynchronizationParameter<CompoundTag, ConstructorBlockEntity> COVER_MANAGER = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "constructor_cover_manager"), EntityDataSerializers.COMPOUND_TAG, new CompoundTag(),
        t -> t.getNode().getCoverManager().writeToNbt(),
        (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
        (initial, p) -> {
        });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(COMPARE)
        .addWatchedParameter(TYPE)
        .addWatchedParameter(DROP)
        .addWatchedParameter(COVER_MANAGER)
        .build();

    public ConstructorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.CONSTRUCTOR.get(), pos, state, SPEC, ConstructorNetworkNode.class);
    }

    @Override
    @Nonnull
    public ConstructorNetworkNode createNode(Level level, BlockPos pos) {
        return new ConstructorNetworkNode(level, pos);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
    }

    @Override
    public CompoundTag writeUpdate(CompoundTag tag) {
        super.writeUpdate(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.getNode().getCoverManager().writeToNbt());

        return tag;
    }

    @Override
    public void readUpdate(CompoundTag tag) {
        super.readUpdate(tag);

        this.getNode().getCoverManager().readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));

        requestModelDataUpdate();

        LevelUtils.updateBlock(level, worldPosition);
    }
}
