package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class ExternalStorageTile extends NetworkNodeTile<ExternalStorageNetworkNode> {
    public static final TileDataParameter<Integer, ExternalStorageTile> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, ExternalStorageTile> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, ExternalStorageTile> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, ExternalStorageTile> TYPE = IType.createParameter();
    public static final TileDataParameter<AccessType, ExternalStorageTile> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, ExternalStorageTile> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long stored = 0;

        for (IExternalStorage<ItemStack> storage : t.getNode().getItemStorages()) {
            stored += storage.getStored();
        }

        for (IExternalStorage<FluidStack> storage : t.getNode().getFluidStorages()) {
            stored += storage.getStored();
        }

        return stored;
    });
    public static final TileDataParameter<Long, ExternalStorageTile> CAPACITY = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long capacity = 0;

        for (IExternalStorage<ItemStack> storage : t.getNode().getItemStorages()) {
            capacity += storage.getCapacity();
        }

        for (IExternalStorage<FluidStack> storage : t.getNode().getFluidStorages()) {
            capacity += storage.getCapacity();
        }

        return capacity;
    });

    public ExternalStorageTile() {
        super(RSTiles.EXTERNAL_STORAGE);
        
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(ACCESS_TYPE);
    }

    @Override
    @Nonnull
    public ExternalStorageNetworkNode createNode(World world, BlockPos pos) {
        return new ExternalStorageNetworkNode(world, pos);
    }
}
