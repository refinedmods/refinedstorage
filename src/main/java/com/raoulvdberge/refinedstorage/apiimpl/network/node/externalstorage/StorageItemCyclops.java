package com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.integration.cyclopscore.SlotlessItemHandlerHelper;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import org.cyclops.cyclopscore.inventory.IndexedSlotlessItemHandlerWrapper;
import org.cyclops.cyclopscore.inventory.SimpleInventory;
import org.cyclops.cyclopscore.tileentity.InventoryTileEntityBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StorageItemCyclops extends StorageItemExternal {
    private NetworkNodeExternalStorage externalStorage;
    private EnumFacing opposite;
    private Supplier<InventoryTileEntityBase> cyclopsInv;
    private int oldInventoryHash = -1;

    public StorageItemCyclops(NetworkNodeExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
        this.opposite = externalStorage.getHolder().getDirection().getOpposite();
        this.cyclopsInv = () -> {
            TileEntity f = externalStorage.getFacingTile();

            return f instanceof InventoryTileEntityBase ? (InventoryTileEntityBase) f : null;
        };
    }

    @Override
    public void detectChanges(INetworkMaster network) {
        InventoryTileEntityBase inv = cyclopsInv.get();
        if (inv != null) {
            int inventoryHash = inv.getInventoryHash();

            if (inventoryHash != oldInventoryHash) {
                super.detectChanges(network);

                oldInventoryHash = inventoryHash;
            }
        }
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return getStacks(cyclopsInv.get());
    }

    @Override
    public int getStored() {
        return getStacks(cyclopsInv.get()).stream().mapToInt(s -> s.getCount()).sum();
    }

    @Override
    public int getPriority() {
        return this.externalStorage.getPriority();
    }

    @Override
    public int getCapacity() {
        InventoryTileEntityBase inv = cyclopsInv.get();

        return inv != null ? inv.getInventory().getSizeInventory() * 64 : 0;
    }

    @Nullable
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, int size, boolean simulate) {
        InventoryTileEntityBase inv = cyclopsInv.get();

        if (inv != null && IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            return RSUtils.transformEmptyToNull(SlotlessItemHandlerHelper.insertItem(inv, opposite, stack, size, simulate));
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public ItemStack extract(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        InventoryTileEntityBase inv = cyclopsInv.get();

        return inv != null ? RSUtils.transformEmptyToNull(SlotlessItemHandlerHelper.extractItem(inv, opposite, stack, size, flags, simulate)) : null;
    }

    private Collection<ItemStack> getStacks(@Nullable InventoryTileEntityBase inv) {
        if (inv != null) {
            if (inv.getInventory() instanceof IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) {
                return ((IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) inv.getInventory())
                    .getIndex()
                    .values()
                    .stream()
                    .flatMap(m -> m.valueCollection().stream())
                    .map(ItemStack::copy)
                    .collect(Collectors.toList());
            } else {
                return Arrays.stream(((SimpleInventory) inv.getInventory()).getItemStacks())
                    .map(ItemStack::copy)
                    .collect(Collectors.toList());
            }
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean isValid(TileEntity facingTE, EnumFacing facing) {
        return facingTE instanceof InventoryTileEntityBase
            && (SlotlessItemHandlerHelper.isSlotless(facingTE, facing)
            || ((InventoryTileEntityBase) facingTE).getInventory() instanceof SimpleInventory);
    }
}
