package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public class NetworkNodeStorageMonitor extends NetworkNode implements IComparable, IType {
    public static final String ID = "storage_monitor";

    private ItemHandlerBasic itemFilter = new ItemHandlerBasic(1, new ItemHandlerListenerNetworkNode(this)) {
        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            RSUtils.updateBlock(holder.world(), holder.pos());
        }
    };

    private ItemHandlerFluid fluidFilter = new ItemHandlerFluid(1, new ItemHandlerListenerNetworkNode(this)) {
        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            RSUtils.updateBlock(holder.world(), holder.pos());
        }
    };

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;

    private int oldAmount = -1;

    public NetworkNodeStorageMonitor(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public void update() {
        super.update();

        int newAmount = getAmount();

        if (oldAmount == -1) {
            oldAmount = newAmount;
        } else if (oldAmount != newAmount) {
            oldAmount = newAmount;

            RSUtils.updateBlock(holder.world(), holder.pos());
        }
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

    public ItemHandlerBasic getItemFilter() {
        return itemFilter;
    }

    public ItemHandlerFluid getFluidFilter() {
        return fluidFilter;
    }

    public int getAmount() {
        if (network == null) {
            return 0;
        }

        switch (type) {
            case ITEMS: {
                ItemStack toCheck = itemFilter.getStackInSlot(0);

                if (toCheck.isEmpty()) {
                    return 0;
                }

                ItemStack stored = network.getItemStorageCache().getList().get(toCheck, compare);

                return stored != null ? stored.getCount() : 0;
            }
            case FLUIDS: {
                FluidStack toCheck = fluidFilter.getFluidStackInSlot(0);

                if (toCheck == null) {
                    return 0;
                }

                FluidStack stored = network.getFluidStorageCache().getList().get(toCheck, compare);

                return stored != null ? stored.amount : 0;
            }
            default: {
                return 0;
            }
        }
    }
}
