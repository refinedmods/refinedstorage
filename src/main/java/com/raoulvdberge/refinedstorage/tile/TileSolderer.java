package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeSolderer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileSolderer extends TileNode<NetworkNodeSolderer> {
    public static final TileDataParameter<Integer> DURATION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileSolderer>() {
        @Override
        public Integer getValue(TileSolderer tile) {
            return tile.getNode().getRecipe() != null ? tile.getNode().getRecipe().getDuration() : 0;
        }
    });

    public static final TileDataParameter<Integer> PROGRESS = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileSolderer>() {
        @Override
        public Integer getValue(TileSolderer tile) {
            return tile.getNode().getProgress();
        }
    });

    public static final TileDataParameter<Boolean> WORKING = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileSolderer>() {
        @Override
        public Boolean getValue(TileSolderer tile) {
            return tile.getNode().isWorking();
        }
    });

    public TileSolderer() {
        dataManager.addWatchedParameter(DURATION);
        dataManager.addWatchedParameter(PROGRESS);
        dataManager.addWatchedParameter(WORKING);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(facing == EnumFacing.DOWN ? getNode().getResult() : getNode().getItems());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nonnull
    public NetworkNodeSolderer createNode() {
        return new NetworkNodeSolderer(this);
    }
}
