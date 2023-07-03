package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.blockentity.config.IAccessType;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IPrioritizable;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class StorageBlockEntity extends NetworkNodeBlockEntity<StorageNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, StorageBlockEntity> PRIORITY = IPrioritizable.createParameter(new ResourceLocation(RS.ID, "storage_priority"));
    public static final BlockEntitySynchronizationParameter<Integer, StorageBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(RS.ID, "storage_compare"));
    public static final BlockEntitySynchronizationParameter<Integer, StorageBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter(new ResourceLocation(RS.ID, "storage_whitelist_blacklist"));
    public static final BlockEntitySynchronizationParameter<AccessType, StorageBlockEntity> ACCESS_TYPE = IAccessType.createParameter(new ResourceLocation(RS.ID, "storage_access_type"));
    public static final BlockEntitySynchronizationParameter<Long, StorageBlockEntity> STORED = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "storage_stored"), RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(PRIORITY)
        .addWatchedParameter(COMPARE)
        .addWatchedParameter(WHITELIST_BLACKLIST)
        .addWatchedParameter(STORED)
        .addWatchedParameter(ACCESS_TYPE)
        .build();

    private final ItemStorageType type;

    public StorageBlockEntity(ItemStorageType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state, SPEC);
        this.type = type;
    }

    public static BlockEntityType<StorageBlockEntity> getType(ItemStorageType type) {
        return switch (type) {
            case ONE_K -> RSBlockEntities.ONE_K_STORAGE_BLOCK.get();
            case FOUR_K -> RSBlockEntities.FOUR_K_STORAGE_BLOCK.get();
            case SIXTEEN_K -> RSBlockEntities.SIXTEEN_K_STORAGE_BLOCK.get();
            case SIXTY_FOUR_K -> RSBlockEntities.SIXTY_FOUR_K_STORAGE_BLOCK.get();
            case CREATIVE -> RSBlockEntities.CREATIVE_STORAGE_BLOCK.get();
        };
    }

    public ItemStorageType getItemStorageType() {
        return type;
    }

    @Override
    @Nonnull
    public StorageNetworkNode createNode(Level level, BlockPos pos) {
        return new StorageNetworkNode(level, pos, type);
    }
}
