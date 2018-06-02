package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.storage.NetworkNodeFluidStorage;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileFluidStorage extends TileNode<NetworkNodeFluidStorage> {
    public static final TileDataParameter<Integer, TileFluidStorage> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, TileFluidStorage> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Boolean, TileFluidStorage> VOID_EXCESS = IExcessVoidable.createParameter();
    public static final TileDataParameter<Integer, TileFluidStorage> MODE = IFilterable.createParameter();
    public static final TileDataParameter<AccessType, TileFluidStorage> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Integer, TileFluidStorage> STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> 0); // TODO

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
    public NetworkNodeFluidStorage createNode(World world, BlockPos pos) {
        return new NetworkNodeFluidStorage(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeFluidStorage.ID;
    }
}

