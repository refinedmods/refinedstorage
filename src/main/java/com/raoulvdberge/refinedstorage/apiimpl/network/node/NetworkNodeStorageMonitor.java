package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeStorageMonitor extends NetworkNode implements IComparable, IType {
    public static final String ID = "storage_monitor";

    private ItemHandlerBasic itemFilter = new ItemHandlerBasic(1, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid fluidFilter = new ItemHandlerFluid(1, new ItemHandlerListenerNetworkNode(this));

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;

    public NetworkNodeStorageMonitor(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getType() {
        return holder.world().isRemote ? TileStorageMonitor.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilter : fluidFilter;
    }
}
