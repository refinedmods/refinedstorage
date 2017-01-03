package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeFluidStorage;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nonnull;

public class TileFluidStorage extends TileNode<NetworkNodeFluidStorage> {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Boolean> VOID_EXCESS = IExcessVoidable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<AccessType> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Integer> STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileFluidStorage>() {
        @Override
        public Integer getValue(TileFluidStorage tile) {
            return StorageDiskFluid.getStored(tile.getNode().getStorageTag());
        }
    });

    public TileFluidStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(VOID_EXCESS);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    @Nonnull
    public NetworkNodeFluidStorage createNode() {
        return new NetworkNodeFluidStorage(this);
    }
}

