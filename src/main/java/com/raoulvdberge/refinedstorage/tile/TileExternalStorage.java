package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageFluidExternal;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageItemExternal;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

public class TileExternalStorage extends TileNode {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<AccessType> ACCESS_TYPE = IAccessType.createParameter();

    public static final TileDataParameter<Integer> STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileExternalStorage>() {
        @Override
        public Integer getValue(TileExternalStorage tile) {
            int stored = 0;

            for (StorageItemExternal storage : ((NetworkNodeExternalStorage) tile.getNode()).getItemStorages()) {
                stored += storage.getStored();
            }

            for (StorageFluidExternal storage : ((NetworkNodeExternalStorage) tile.getNode()).getFluidStorages()) {
                stored += storage.getStored();
            }

            return stored;
        }
    });

    public static final TileDataParameter<Integer> CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileExternalStorage>() {
        @Override
        public Integer getValue(TileExternalStorage tile) {
            int capacity = 0;

            for (StorageItemExternal storage : ((NetworkNodeExternalStorage) tile.getNode()).getItemStorages()) {
                capacity += storage.getCapacity();
            }

            for (StorageFluidExternal storage : ((NetworkNodeExternalStorage) tile.getNode()).getFluidStorages()) {
                capacity += storage.getCapacity();
            }

            return capacity;
        }
    });

    public TileExternalStorage() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    public INetworkNode createNode() {
        return new NetworkNodeExternalStorage(this);
    }
}
