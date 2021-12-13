package com.refinedmods.refinedstorage.blockentity.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IAccessType {
    static <T extends BlockEntity & INetworkNodeProxy<?>> BlockEntitySynchronizationParameter<AccessType, T> createParameter() {
        return new BlockEntitySynchronizationParameter<>(RSSerializers.ACCESS_TYPE_SERIALIZER, AccessType.INSERT_EXTRACT, t -> ((IAccessType) t.getNode()).getAccessType(), (t, v) -> ((IAccessType) t.getNode()).setAccessType(v));
    }

    AccessType getAccessType();

    void setAccessType(AccessType accessType);
}