package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import org.cyclops.cyclopscore.inventory.IndexedSlotlessItemHandlerWrapper;
import org.cyclops.cyclopscore.tileentity.InventoryTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemStorageCyclops extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private EnumFacing opposite;
    private Supplier<InventoryTileEntity> cyclopsInv;
    private int oldInventoryHash = -1;

    public ItemStorageCyclops(TileExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
        this.opposite = externalStorage.getDirection().getOpposite();
        this.cyclopsInv = () -> (InventoryTileEntity) externalStorage.getFacingTile();
    }

    @Override
    public void detectChanges(INetworkMaster network) {
        InventoryTileEntity inv = cyclopsInv.get();
        if (inv != null) {
            int inventoryHash = inv.getInventoryHash();
            if (inventoryHash != oldInventoryHash) {
                super.detectChanges(network);
                oldInventoryHash = inventoryHash;
            }
        }
    }

    @Override
    public List<ItemStack> getStacks() {
        return getStacks(cyclopsInv.get());
    }

    @Override
    public int getStored() {
        return getStacks(cyclopsInv.get()).stream().mapToInt(s -> s.stackSize).sum();
    }

    @Override
    public int getPriority() {
        return this.externalStorage.getPriority();
    }

    @Override
    public int getCapacity() {
        InventoryTileEntity inv = cyclopsInv.get();

        return inv != null ? inv.getInventory().getSizeInventory() * 64 : 0;
    }

    @Nullable
    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        InventoryTileEntity inv = cyclopsInv.get();

        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            ISlotlessItemHandler slotlessItemHandler = inv.getCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite);
            return slotlessItemHandler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        InventoryTileEntity inv = cyclopsInv.get();

        ISlotlessItemHandler slotlessItemHandler = inv.getCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite);
        return slotlessItemHandler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
    }

    private List<ItemStack> getStacks(@Nullable InventoryTileEntity inv) {
        if (inv != null) {
            if (inv.getInventory() instanceof IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) {
                return ((IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) inv.getInventory())
                        .getIndex().values().stream().flatMap(m -> m.valueCollection().stream()).map(ItemStack::copy).collect(Collectors.toList());
            } else {
                return Arrays.stream(inv.getInventory().getItemStacks()).map(ItemStack::copy).collect(Collectors.toList());
            }
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean isValid(TileEntity facingTE, EnumFacing facing) {
        return facingTE instanceof InventoryTileEntity
                && facingTE.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, facing);
    }
}
