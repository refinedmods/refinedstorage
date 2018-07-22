package com.raoulvdberge.refinedstorage.tile.config;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameterClientListener;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IType {
    int ITEMS = 0;
    int FLUIDS = 1;

    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<Integer, T> createParameter(@Nullable TileDataParameterClientListener<Integer> clientListener) {
        return new TileDataParameter<>(DataSerializers.VARINT, ITEMS, t -> ((IType) t.getNode()).getType(), (t, v) -> {
            if (v == IType.ITEMS || v == IType.FLUIDS) {
                ((IType) t.getNode()).setType(v);

                t.getWorld().playerEntities.stream()
                    .filter(p -> p.openContainer instanceof ContainerBase && ((ContainerBase) p.openContainer).getTile().getPos().equals(t.getPos()))
                    .forEach(p -> p.openContainer.detectAndSendChanges());
            }
        }, clientListener);
    }

    static <T extends TileEntity & INetworkNodeProxy> TileDataParameter<Integer, T> createParameter() {
        return createParameter(null);
    }

    int getType();

    void setType(int type);

    IItemHandler getFilterInventory();
}
