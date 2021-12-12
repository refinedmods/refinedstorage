package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IComparable {
    static <T extends BlockEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> ((IComparable) t.getNode()).getCompare(), (t, v) -> ((IComparable) t.getNode()).setCompare(v));
    }

    int getCompare();

    void setCompare(int compare);
}
