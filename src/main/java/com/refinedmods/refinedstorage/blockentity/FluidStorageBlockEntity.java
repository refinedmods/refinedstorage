package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
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

public class FluidStorageBlockEntity extends NetworkNodeBlockEntity<FluidStorageNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, FluidStorageBlockEntity> PRIORITY = IPrioritizable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, FluidStorageBlockEntity> COMPARE = IComparable.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, FluidStorageBlockEntity> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final BlockEntitySynchronizationParameter<AccessType, FluidStorageBlockEntity> ACCESS_TYPE = IAccessType.createParameter();
    public static final BlockEntitySynchronizationParameter<Long, FluidStorageBlockEntity> STORED = new BlockEntitySynchronizationParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    private final FluidStorageType type;

    public FluidStorageBlockEntity(FluidStorageType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state);

        this.type = type;

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    public static BlockEntityType<FluidStorageBlockEntity> getType(FluidStorageType type) {
        switch (type) {
            case SIXTY_FOUR_K:
                return RSBlockEntities.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK;
            case TWO_HUNDRED_FIFTY_SIX_K:
                return RSBlockEntities.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK;
            case THOUSAND_TWENTY_FOUR_K:
                return RSBlockEntities.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK;
            case FOUR_THOUSAND_NINETY_SIX_K:
                return RSBlockEntities.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK;
            case CREATIVE:
                return RSBlockEntities.CREATIVE_FLUID_STORAGE_BLOCK;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }

    public FluidStorageType getFluidStorageType() {
        return type;
    }

    @Override
    @Nonnull
    public FluidStorageNetworkNode createNode(Level level, BlockPos pos) {
        return new FluidStorageNetworkNode(level, pos, type);
    }
}

