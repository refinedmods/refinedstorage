package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.DestructorNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;

public class DestructorBlockEntity extends NetworkNodeBlockEntity<DestructorNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, DestructorBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(RS.ID, "destructor_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, DestructorBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter(new ResourceLocation(RS.ID, "destructor_whitelist_blacklist"));
    public static final BlockEntitySynchronizationParameter<Integer, DestructorBlockEntity> TYPE = IType.createParameter(new ResourceLocation(RS.ID, "destructor_type"));
    public static final BlockEntitySynchronizationParameter<Boolean, DestructorBlockEntity> PICKUP = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "destructor_pickup"), EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isPickupItem(), (t, v) -> {
        t.getNode().setPickupItem(v);
        t.getNode().markDirty();
    });

    public static final BlockEntitySynchronizationParameter<CompoundTag, DestructorBlockEntity> COVER_MANAGER = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "destructor_cover_manager"), EntityDataSerializers.COMPOUND_TAG, new CompoundTag(),
        t -> t.getNode().getCoverManager().writeToNbt(),
        (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
        (initial, p) -> {
        });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(COMPARE)
        .addWatchedParameter(WHITELIST_BLACKLIST)
        .addWatchedParameter(TYPE)
        .addWatchedParameter(PICKUP)
        .addWatchedParameter(COVER_MANAGER)
        .build();

    public DestructorBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.DESTRUCTOR.get(), pos, state, SPEC, DestructorNetworkNode.class);
    }

    @Override
    @Nonnull
    public DestructorNetworkNode createNode(Level level, BlockPos pos) {
        return new DestructorNetworkNode(level, pos);
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
