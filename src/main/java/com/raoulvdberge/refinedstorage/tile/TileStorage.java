package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.NetworkNodeStorage;
import com.raoulvdberge.refinedstorage.tile.config.IAccessType;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IPrioritizable;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileStorage extends TileNode<NetworkNodeStorage> {
    public static final TileDataParameter<Integer, TileStorage> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, TileStorage> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileStorage> MODE = IFilterable.createParameter();
    public static final TileDataParameter<AccessType, TileStorage> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, TileStorage> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> t.getNode().getStorage() != null ? (long) t.getNode().getStorage().getStored() : 0);

    public TileStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    @Nonnull
    public NetworkNodeStorage createNode(World world, BlockPos pos) {
        return new NetworkNodeStorage(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeStorage.ID;
    }
}
