package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import com.refinedmods.refinedstorage.tile.config.IAccessType;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IPrioritizable;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class StorageTile extends NetworkNodeTile<StorageNetworkNode> {
    public static final TileDataParameter<Integer, StorageTile> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, StorageTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, StorageTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<AccessType, StorageTile> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, StorageTile> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    private final ItemStorageType type;

    public StorageTile(ItemStorageType type) {
        super(getType(type));

        this.type = type;

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    public static TileEntityType<StorageTile> getType(ItemStorageType type) {
        switch (type) {
            case ONE_K:
                return RSTiles.ONE_K_STORAGE_BLOCK;
            case FOUR_K:
                return RSTiles.FOUR_K_STORAGE_BLOCK;
            case SIXTEEN_K:
                return RSTiles.SIXTEEN_K_STORAGE_BLOCK;
            case SIXTY_FOUR_K:
                return RSTiles.SIXTY_FOUR_K_STORAGE_BLOCK;
            case CREATIVE:
                return RSTiles.CREATIVE_STORAGE_BLOCK;
            default:
                throw new IllegalArgumentException("Unknown storage type " + type);
        }
    }

    public ItemStorageType getItemStorageType() {
        return type;
    }

    @Override
    @Nonnull
    public StorageNetworkNode createNode(World world, BlockPos pos) {
        return new StorageNetworkNode(world, pos, type);
    }
}
