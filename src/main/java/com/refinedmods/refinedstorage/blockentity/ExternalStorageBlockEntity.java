package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.blockentity.config.*;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.config.ServerConfig;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;

public class ExternalStorageBlockEntity extends NetworkNodeBlockEntity<ExternalStorageNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, ExternalStorageBlockEntity> PRIORITY = IPrioritizable.createParameter(new ResourceLocation(RS.ID, "external_storage_priority"));
    public static final BlockEntitySynchronizationParameter<Integer, ExternalStorageBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(RS.ID, "external_storage_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, ExternalStorageBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter(new ResourceLocation(RS.ID, "external_storage_whitelist_blacklist"));
    public static final BlockEntitySynchronizationParameter<Integer, ExternalStorageBlockEntity> TYPE = IType.createParameter(new ResourceLocation(RS.ID, "external_storage_type"));
    public static final BlockEntitySynchronizationParameter<AccessType, ExternalStorageBlockEntity> ACCESS_TYPE = IAccessType.createParameter(new ResourceLocation(RS.ID, "external_storage_access_type"));
    public static final BlockEntitySynchronizationParameter<Long, ExternalStorageBlockEntity> STORED = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "external_storage_stored"), RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long stored = 0;

        for (IExternalStorage<ItemStack> storage : t.getNode().getItemStorages()) {
            stored += storage.getStored();
        }

        for (IExternalStorage<FluidStack> storage : t.getNode().getFluidStorages()) {
            stored += storage.getStored();
        }

        return stored;
    });
    public static final BlockEntitySynchronizationParameter<Long, ExternalStorageBlockEntity> CAPACITY = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "external_storage_capacity"), RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long capacity = 0;

        for (IExternalStorage<ItemStack> storage : t.getNode().getItemStorages()) {
            capacity += storage.getCapacity();
        }

        for (IExternalStorage<FluidStack> storage : t.getNode().getFluidStorages()) {
            capacity += storage.getCapacity();
        }

        return capacity;
    });

    public static final BlockEntitySynchronizationParameter<CompoundTag, ExternalStorageBlockEntity> COVER_MANAGER = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "external_storage_cover_manager"), EntityDataSerializers.COMPOUND_TAG, new CompoundTag(), t -> t.getNode().getCoverManager().writeToNbt(), (t, v) -> t.getNode().getCoverManager().readFromNbt(v), (initial, p) -> {
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(PRIORITY)
        .addWatchedParameter(COMPARE)
        .addWatchedParameter(WHITELIST_BLACKLIST)
        .addWatchedParameter(STORED)
        .addWatchedParameter(CAPACITY)
        .addWatchedParameter(TYPE)
        .addWatchedParameter(ACCESS_TYPE)
        .addWatchedParameter(COVER_MANAGER)
        .build();

    public ExternalStorageBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.EXTERNAL_STORAGE.get(), pos, state, SPEC, ExternalStorageNetworkNode.class);
    }

    @Override
    @Nonnull
    public ExternalStorageNetworkNode createNode(Level level, BlockPos pos) {
        return new ExternalStorageNetworkNode(level, pos);
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
