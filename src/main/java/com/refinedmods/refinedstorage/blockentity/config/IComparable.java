package com.refinedmods.refinedstorage.blockentity.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IComparable {
    static <T extends BlockEntity & INetworkNodeProxy<?>> BlockEntitySynchronizationParameter<Integer, T> createParameter() {
        return new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> ((IComparable) t.getNode()).getCompare(), (t, v) -> ((IComparable) t.getNode()).setCompare(v));
    }

    int getCompare();

    void setCompare(int compare);
}
