package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class StorageMonitorNetworkNode extends NetworkNode implements IComparable {
    public static final int DEPOSIT_ALL_MAX_DELAY = 500;

    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "storage_monitor");

    private static final String NBT_COMPARE = "Compare";

    private BaseItemHandler itemFilter = new BaseItemHandler(1)
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading) {
                WorldUtils.updateBlock(world, pos);
            }
        });

    private Map<String, Pair<ItemStack, Long>> deposits = new HashMap<>();

    private int compare = IComparer.COMPARE_NBT;

    private int oldAmount = -1;

    public StorageMonitorNetworkNode(World world, BlockPos pos) {
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

    public boolean depositAll(PlayerEntity player) {
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

        if (System.currentTimeMillis() - insertedAt < DEPOSIT_ALL_MAX_DELAY) {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack toInsert = player.inventory.getStackInSlot(i);

                if (API.instance().getComparer().isEqual(inserted, toInsert, compare)) {
                    player.inventory.setInventorySlotContents(i, network.insertItemTracked(toInsert, toInsert.getCount()));
                }
            }
        }

        return true;
    }

    public boolean deposit(PlayerEntity player, ItemStack toInsert) {
        if (network == null) {
            return false;
        }

        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return false;
        }

        ItemStack filter = itemFilter.getStackInSlot(0);

        if (!filter.isEmpty() && API.instance().getComparer().isEqual(filter, toInsert, compare)) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, network.insertItemTracked(toInsert, toInsert.getCount()));

            deposits.put(player.getGameProfile().getName(), Pair.of(toInsert, System.currentTimeMillis()));
        }

        return true;
    }

    public void extract(PlayerEntity player, Direction side) {
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

            if (!result.isEmpty() && !player.inventory.addItemStackToInventory(result.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), result);
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getStorageMonitor().getUsage();
    }

    @Override
    public ResourceLocation getId() {
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
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        tag.putInt(NBT_COMPARE, compare);

        StackUtils.writeItems(itemFilter, 0, tag);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
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

    public BaseItemHandler getItemFilters() {
        return itemFilter;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }
}
