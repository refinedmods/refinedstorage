package com.refinedmods.refinedstorage.tile.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public interface IType {
    int ITEMS = 0;
    int FLUIDS = 1;

    static <T extends TileEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter(@Nullable TileDataParameterClientListener<Integer> clientListener) {
        return new TileDataParameter<>(DataSerializers.INT, ITEMS, t -> ((IType) t.getNode()).getType(), (t, v) -> {
            if (v == IType.ITEMS || v == IType.FLUIDS) {
                ((IType) t.getNode()).setType(v);
            }
        }, clientListener);
    }

    static <T extends TileEntity & INetworkNodeProxy<?>> TileDataParameter<Integer, T> createParameter() {
        return createParameter(null);
    }

    int getType();

    void setType(int type);

    IItemHandlerModifiable getItemFilters();

    FluidInventory getFluidFilters();
}
