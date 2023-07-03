package com.refinedmods.refinedstorage.blockentity.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IAccessType {
    static <T extends BlockEntity & INetworkNodeProxy<?>> BlockEntitySynchronizationParameter<AccessType, T> createParameter(ResourceLocation id) {
        return new BlockEntitySynchronizationParameter<>(id, RSSerializers.ACCESS_TYPE_SERIALIZER, AccessType.INSERT_EXTRACT, t -> ((IAccessType) t.getNode()).getAccessType(), (t, v) -> ((IAccessType) t.getNode()).setAccessType(v));
    }

    AccessType getAccessType();

    void setAccessType(AccessType accessType);
}