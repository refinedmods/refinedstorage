package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.integration.cyclopscore.IntegrationCyclopsCore;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemHandlerHelper;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import org.cyclops.cyclopscore.inventory.IndexedSlotlessItemHandlerWrapper;
import org.cyclops.cyclopscore.inventory.SimpleInventory;
import org.cyclops.cyclopscore.tileentity.InventoryTileEntityBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemStorageCyclops extends ItemStorageExternal {
    private TileExternalStorage externalStorage;
    private EnumFacing opposite;
    private Supplier<InventoryTileEntityBase> cyclopsInv;
    private int oldInventoryHash = -1;

    public ItemStorageCyclops(TileExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
        this.opposite = externalStorage.getDirection().getOpposite();
        this.cyclopsInv = () -> (InventoryTileEntityBase) externalStorage.getFacingTile();
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
        InventoryTileEntityBase inv = cyclopsInv.get();

        return inv != null ? inv.getInventory().getSizeInventory() * 64 : 0;
    }

    @Nullable
    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        InventoryTileEntityBase inv = cyclopsInv.get();

        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            ISlotlessItemHandler slotlessItemHandler = inv.getCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite);
            ItemStack remainder = slotlessItemHandler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
            int remainderCount = -1;
            if (remainder != null && remainder.stackSize != remainderCount) {
                remainderCount = remainder.stackSize;
                remainder = slotlessItemHandler.insertItem(remainder.copy(), simulate);
            }
            return remainder;
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        InventoryTileEntityBase inv = cyclopsInv.get();

        ISlotlessItemHandler slotlessItemHandler = inv.getCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite);
        ItemStack extracted = slotlessItemHandler.extractItem(ItemHandlerHelper.copyStackWithSize(stack, size), IntegrationCyclopsCore.comparerFlagsToItemMatch(flags), simulate);
        while (extracted.stackSize < size) {
            ItemStack extraExtract = slotlessItemHandler.extractItem(ItemHandlerHelper.copyStackWithSize(extracted, size - extracted.stackSize), IntegrationCyclopsCore.comparerFlagsToItemMatch(flags), simulate);
            if (extraExtract != null) {
                extracted.stackSize += extraExtract.stackSize;
            } else {
                // Nothing more to extract
                break;
            }
        }
        return extracted;
    }

    private List<ItemStack> getStacks(@Nullable InventoryTileEntityBase inv) {
        if (inv != null) {
            if (inv.getInventory() instanceof IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) {
                return ((IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) inv.getInventory())
                        .getIndex().values().stream().flatMap(m -> m.valueCollection().stream()).map(ItemStack::copy).collect(Collectors.toList());
            } else {
                return Arrays.stream(((SimpleInventory)inv.getInventory()).getItemStacks()).map(ItemStack::copy).collect(Collectors.toList());
            }
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean isValid(TileEntity facingTE, EnumFacing facing) {
        return facingTE instanceof InventoryTileEntityBase
                && (facingTE.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, facing)
                    || ((InventoryTileEntityBase) facingTE).getInventory() instanceof SimpleInventory);
    }
}
