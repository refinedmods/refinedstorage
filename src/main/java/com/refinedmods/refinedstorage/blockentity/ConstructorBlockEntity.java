package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class ConstructorBlockEntity extends NetworkNodeBlockEntity<ConstructorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, ConstructorBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, ConstructorBlockEntity> TYPE = IType.createParameter();
    public static final BlockEntitySynchronizationParameter<Boolean, ConstructorBlockEntity> DROP = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    public static final BlockEntitySynchronizationParameter<CompoundTag, ConstructorBlockEntity> COVER_MANAGER = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.COMPOUND_TAG, new CompoundTag(),
        t -> t.getNode().getCoverManager().writeToNbt(),
        (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
        (initial, p) -> {
        });

    public ConstructorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.CONSTRUCTOR, pos, state, ConstructorNetworkNode.class);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
        dataManager.addWatchedParameter(COVER_MANAGER);
    }

    @Override
    @Nonnull
    public ConstructorNetworkNode createNode(Level level, BlockPos pos) {
        return new ConstructorNetworkNode(level, pos);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
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
