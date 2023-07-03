package com.refinedmods.refinedstorage.blockentity.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPrioritizable {
    static <T extends BlockEntity & INetworkNodeProxy<?>> BlockEntitySynchronizationParameter<Integer, T> createParameter(ResourceLocation id) {
        return new BlockEntitySynchronizationParameter<>(id, EntityDataSerializers.INT, 0, t -> ((IPrioritizable) t.getNode()).getPriority(), (t, v) -> ((IPrioritizable) t.getNode()).setPriority(v));
    }

    int getPriority();

    void setPriority(int priority);
}
