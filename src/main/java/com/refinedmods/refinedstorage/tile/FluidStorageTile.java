package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import com.refinedmods.refinedstorage.tile.config.IAccessType;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IPrioritizable;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class FluidStorageTile extends NetworkNodeTile<FluidStorageNetworkNode> {
    public static final TileDataParameter<Integer, FluidStorageTile> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, FluidStorageTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, FluidStorageTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<AccessType, FluidStorageTile> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, FluidStorageTile> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    private final FluidStorageType type;

    public FluidStorageTile(FluidStorageType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state);

        this.type = type;

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    public static BlockEntityType<FluidStorageTile> getType(FluidStorageType type) {
        switch (type) {
            case SIXTY_FOUR_K:
                return RSTiles.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK;
            case TWO_HUNDRED_FIFTY_SIX_K:
                return RSTiles.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK;
            case THOUSAND_TWENTY_FOUR_K:
                return RSTiles.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK;
            case FOUR_THOUSAND_NINETY_SIX_K:
                return RSTiles.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK;
            case CREATIVE:
                return RSTiles.CREATIVE_FLUID_STORAGE_BLOCK;
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

