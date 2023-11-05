package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.blockentity.config.IAccessType;
import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.config.IPrioritizable;
import com.refinedmods.refinedstorage.blockentity.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class StorageBlockEntity extends NetworkNodeBlockEntity<StorageNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, StorageBlockEntity> PRIORITY = IPrioritizable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, StorageBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, StorageBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final BlockEntitySynchronizationParameter<AccessType, StorageBlockEntity> ACCESS_TYPE = IAccessType.createParameter();
    public static final BlockEntitySynchronizationParameter<Long, StorageBlockEntity> STORED = new BlockEntitySynchronizationParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    private final ItemStorageType type;

    public StorageBlockEntity(ItemStorageType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state, StorageNetworkNode.class);

        this.type = type;

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    public static BlockEntityType<StorageBlockEntity> getType(ItemStorageType type) {
        switch (type) {
            case ONE_K:
                return RSBlockEntities.ONE_K_STORAGE_BLOCK;
            case FOUR_K:
                return RSBlockEntities.FOUR_K_STORAGE_BLOCK;
            case SIXTEEN_K:
                return RSBlockEntities.SIXTEEN_K_STORAGE_BLOCK;
            case SIXTY_FOUR_K:
                return RSBlockEntities.SIXTY_FOUR_K_STORAGE_BLOCK;
            case CREATIVE:
                return RSBlockEntities.CREATIVE_STORAGE_BLOCK;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
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
