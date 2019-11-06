package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.FluidStorageType;
import com.raoulvdberge.refinedstorage.tile.config.IAccessType;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IPrioritizable;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.FluidStorageBlockUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class FluidStorageTile extends NetworkNodeTile<FluidStorageNetworkNode> {
    public static final TileDataParameter<Integer, FluidStorageTile> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, FluidStorageTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, FluidStorageTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<AccessType, FluidStorageTile> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, FluidStorageTile> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    private final FluidStorageType type;

    public FluidStorageTile(FluidStorageType type) {
        super(FluidStorageBlockUtils.getTileEntityType(type));

        this.type = type;

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    public FluidStorageType getFluidStorageType() {
        return type;
    }

    @Override
    @Nonnull
    public FluidStorageNetworkNode createNode(World world, BlockPos pos) {
        return new FluidStorageNetworkNode(world, pos, type);
    }
}

