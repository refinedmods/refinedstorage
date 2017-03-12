package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class NetworkNodeStorageMonitor extends NetworkNode implements IComparable, IType {
    public static final int DEPOSIT_ALL_MAX_DELAY = 500;

    public static final String ID = "storage_monitor";

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";

    private ItemHandlerBase itemFilter = new ItemHandlerBase(1, new ItemHandlerListenerNetworkNode(this)) {
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

    private Map<String, Pair<ItemStack, Long>> deposits = new HashMap<>();

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

    public boolean depositAll(EntityPlayer player) {
        if (type != IType.ITEMS || network == null) {
            return false;
        }

        Pair<ItemStack, Long> deposit = deposits.get(player.getGameProfile().getName());

        if (deposit == null) {
            return false;
        }

        ItemStack inserted = deposit.getKey();
        long insertedAt = deposit.getValue();

        if (Minecraft.getSystemTime() - insertedAt < DEPOSIT_ALL_MAX_DELAY) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack toInsert = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(inserted, toInsert, compare)) {
                    player.inventory.setInventorySlotContents(i, RSUtils.transformNullToEmpty(network.insertItemTracked(toInsert, toInsert.getCount())));
                }
            }
        }

        return true;
    }

    public boolean deposit(EntityPlayer player, ItemStack toInsert) {
        if (type != IType.ITEMS) {
            return false;
        }

        ItemStack filter = itemFilter.getStackInSlot(0);

        if (network != null && !filter.isEmpty() && API.instance().getComparer().isEqual(filter, toInsert, compare)) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, RSUtils.transformNullToEmpty(network.insertItemTracked(toInsert, toInsert.getCount())));

            deposits.put(player.getGameProfile().getName(), Pair.of(toInsert, Minecraft.getSystemTime()));
        }

        return true;
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

        RSUtils.updateBlock(holder.world(), holder.pos());

        markDirty();
    }

    @Override
    public int getType() {
        return holder.world().isRemote ? TileStorageMonitor.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        RSUtils.updateBlock(holder.world(), holder.pos());

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilter : fluidFilter;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_TYPE, type);

        RSUtils.writeItems(itemFilter, 0, tag);
        RSUtils.writeItems(fluidFilter, 1, tag);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        RSUtils.readItems(itemFilter, 0, tag);
        RSUtils.readItems(fluidFilter, 1, tag);
    }

    public ItemHandlerBase getItemFilter() {
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

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
