package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPrioritizable {
    static <T extends BlockEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter() {
        return new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> ((IPrioritizable) t.getNode()).getPriority(), (t, v) -> ((IPrioritizable) t.getNode()).setPriority(v));
    }

    int getPriority();

    void setPriority(int priority);
}
