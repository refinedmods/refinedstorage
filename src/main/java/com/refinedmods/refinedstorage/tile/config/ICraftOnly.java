package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;

public interface ICraftOnly {
    static <T extends TileEntity & INetworkNodeProxy<?>> TileDataParameter<Boolean, T> createParameter() {
        return new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> ((ICraftOnly) t.getNode()).isCraftOnly(), (t, v) -> ((ICraftOnly) t.getNode()).setCraftOnly(v));
    }

    boolean isCraftOnly();

    void setCraftOnly(boolean craftOnly);
}
