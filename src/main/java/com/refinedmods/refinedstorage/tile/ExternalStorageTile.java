package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.tile.config.*;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
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

    public static final TileDataParameter<CompoundNBT, ExternalStorageTile> COVER_MANAGER = new TileDataParameter<>(DataSerializers.COMPOUND_TAG, new CompoundNBT(),
            t -> t.getNode().getCoverManager().writeToNbt(),
            (t, v) -> t.getNode().getCoverManager().readFromNbt(v),
            (initial, p) -> {});

    public ExternalStorageTile() {
        super(RSTiles.EXTERNAL_STORAGE);

        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(ACCESS_TYPE);
        dataManager.addWatchedParameter(COVER_MANAGER);
    }

    @Override
    @Nonnull
    public ExternalStorageNetworkNode createNode(World world, BlockPos pos) {
        return new ExternalStorageNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(CoverManager.PROPERTY, this.getNode().getCoverManager()).build();
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        tag.put(CoverManager.NBT_COVER_MANAGER, this.getNode().getCoverManager().writeToNbt());

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        this.getNode().getCoverManager().readFromNbt(tag.getCompound(CoverManager.NBT_COVER_MANAGER));

        requestModelDataUpdate();

        WorldUtils.updateBlock(level, worldPosition);
    }
}
