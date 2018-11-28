package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExternalStorage;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class TileExternalStorage extends TileNode<NetworkNodeExternalStorage> {
    public static final TileDataParameter<Integer, TileExternalStorage> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, TileExternalStorage> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileExternalStorage> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer, TileExternalStorage> TYPE = IType.createParameter();
    public static final TileDataParameter<AccessType, TileExternalStorage> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, TileExternalStorage> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long stored = 0;

        for (IStorageExternal<ItemStack> storage : t.getNode().getItemStorages()) {
            stored += storage.getStored();
        }

        for (IStorageExternal<FluidStack> storage : t.getNode().getFluidStorages()) {
            stored += storage.getStored();
        }

        return stored;
    });
    public static final TileDataParameter<Long, TileExternalStorage> CAPACITY = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long capacity = 0;

        for (IStorageExternal<ItemStack> storage : t.getNode().getItemStorages()) {
            capacity += storage.getCapacity();
        }

        for (IStorageExternal<FluidStack> storage : t.getNode().getFluidStorages()) {
            capacity += storage.getCapacity();
        }

        return capacity;
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
    @Nonnull
    public NetworkNodeExternalStorage createNode(World world, BlockPos pos) {
        return new NetworkNodeExternalStorage(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeExternalStorage.ID;
    }
}
