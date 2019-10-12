package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import com.raoulvdberge.refinedstorage.tile.config.IAccessType;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IPrioritizable;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.StorageBlockUtils;
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
        super(StorageBlockUtils.getTileEntityType(type));

        this.type = type;
        
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
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
