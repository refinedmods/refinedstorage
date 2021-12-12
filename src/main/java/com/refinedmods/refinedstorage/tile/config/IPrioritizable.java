package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public interface IPrioritizable {
    static <T extends TileEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.INT, 0, t -> ((IPrioritizable) t.getNode()).getPriority(), (t, v) -> ((IPrioritizable) t.getNode()).setPriority(v));
    }

    int getPriority();

    void setPriority(int priority);
}
