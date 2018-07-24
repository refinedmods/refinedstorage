package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class NetworkNodeStorageMonitor extends NetworkNode implements IComparable {
    public static final int DEPOSIT_ALL_MAX_DELAY = 500;

    public static final String ID = "storage_monitor";

    private static final String NBT_COMPARE = "Compare";

    private ItemHandlerBase itemFilter = new ItemHandlerBase(1, new ListenerNetworkNode(this)) {
        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            WorldUtils.updateBlock(world, pos);
        }
    };

    private Map<String, Pair<ItemStack, Long>> deposits = new HashMap<>();

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;

    private int oldAmount = -1;

    public NetworkNodeStorageMonitor(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void update() {
        super.update();

        int newAmount = getAmount();

        if (oldAmount == -1) {
            oldAmount = newAmount;
        } else if (oldAmount != newAmount) {
            oldAmount = newAmount;

            WorldUtils.updateBlock(world, pos);
        }
    }

    public boolean depositAll(EntityPlayer player) {
        if (network == null) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return false;
        }

        Pair<ItemStack, Long> deposit = deposits.get(player.getGameProfile().getName());

        if (deposit == null) {
            return false;
        }

        ItemStack inserted = deposit.getKey();
        long insertedAt = deposit.getValue();

        if (MinecraftServer.getCurrentTimeMillis() - insertedAt < DEPOSIT_ALL_MAX_DELAY) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack toInsert = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(inserted, toInsert, compare)) {
                    player.inventory.setInventorySlotContents(i, StackUtils.nullToEmpty(network.insertItemTracked(toInsert, toInsert.getCount())));
                }
            }
        }

        return true;
    }

    public boolean deposit(EntityPlayer player, ItemStack toInsert) {
        if (network == null) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return false;
        }

        ItemStack filter = itemFilter.getStackInSlot(0);

        if (!filter.isEmpty() && API.instance().getComparer().isEqual(filter, toInsert, compare)) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, StackUtils.nullToEmpty(network.insertItemTracked(toInsert, toInsert.getCount())));

            deposits.put(player.getGameProfile().getName(), Pair.of(toInsert, MinecraftServer.getCurrentTimeMillis()));
        }

        return true;
    }

    public void extract(EntityPlayer player, EnumFacing side) {
        if (network == null || getDirection() != side) {
            return;
        }

        if (!network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }

        ItemStack filter = itemFilter.getStackInSlot(0);

        int toExtract = player.isSneaking() ? 1 : 64;

        if (!filter.isEmpty()) {
            ItemStack result = network.extractItem(filter, toExtract, compare, Action.PERFORM);

            if (result != null) {
                if (!player.inventory.addItemStackToInventory(result.copy())) {
                    InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), result);
                }
            }
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

        WorldUtils.updateBlock(world, pos);

        markDirty();
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        tag.setInteger(NBT_COMPARE, compare);

        StackUtils.writeItems(itemFilter, 0, tag);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        StackUtils.readItems(itemFilter, 0, tag);
    }

    public int getAmount() {
        if (network == null) {
            return 0;
        }

        ItemStack toCheck = itemFilter.getStackInSlot(0);

        if (toCheck.isEmpty()) {
            return 0;
        }

        ItemStack stored = network.getItemStorageCache().getList().get(toCheck, compare);

        return stored != null ? stored.getCount() : 0;
    }

    public ItemHandlerBase getItemFilters() {
        return itemFilter;
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
