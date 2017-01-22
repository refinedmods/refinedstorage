package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import gnu.trove.map.TIntObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.cyclops.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import org.cyclops.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import org.cyclops.cyclopscore.inventory.IndexedInventory;
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

    public ItemStorageCyclops(TileExternalStorage externalStorage) {
        this.externalStorage = externalStorage;
        this.opposite = externalStorage.getDirection().getOpposite();
        this.cyclopsInv = () -> (InventoryTileEntity) externalStorage.getFacingTile();
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
            if (inv.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite)) {
                ISlotlessItemHandler slotlessItemHandler = inv.getCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite);
                return slotlessItemHandler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
            } else if (inv.getInventory() instanceof IndexedInventory) {
                IndexedInventory indexedInv = (IndexedInventory) inv.getInventory();
                return insertItem(inv, indexedInv, stack, size, simulate);
            } else {
                return ItemHandlerHelper.insertItem(RSUtils.getItemHandler(inv, opposite), ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Nullable
    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        InventoryTileEntity inv = cyclopsInv.get();

        if (IFilterable.canTake(externalStorage.getItemFilters(), externalStorage.getMode(), externalStorage.getCompare(), stack)) {
            if (inv.hasCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite)) {
                ISlotlessItemHandler slotlessItemHandler = inv.getCapability(SlotlessItemHandlerConfig.CAPABILITY, opposite);
                return slotlessItemHandler.insertItem(ItemHandlerHelper.copyStackWithSize(stack, size), simulate);
            } else if (inv.getInventory() instanceof IndexedInventory) {
                IndexedInventory indexedInv = (IndexedInventory) inv.getInventory();
                return extractItem(inv, indexedInv, stack, size, flags, simulate);
            } else {
                IItemHandler itemHandler = RSUtils.getItemHandler(inv, opposite);
                return ItemStorageItemHandler.extractItem(itemHandler, stack, size, flags, simulate);
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    private List<ItemStack> getStacks(@Nullable InventoryTileEntity inv) {
        if (inv != null) {
            if (inv.getInventory() instanceof IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) {
                return ((IndexedSlotlessItemHandlerWrapper.IInventoryIndexReference) inv.getInventory())
                        .getIndex().values().stream().flatMap(m -> m.valueCollection().stream()).collect(Collectors.toList());
            } else {
                return Arrays.asList(inv.getInventory().getItemStacks());
            }
        } else {
            return Collections.emptyList();
        }
    }

    private ItemStack insertItem(InventoryTileEntity inv, IndexedInventory indexedInv, ItemStack stack, int size, boolean simulate) {
        TIntObjectMap<ItemStack> slotStackMap = indexedInv.getIndex().get(stack.getItem());
        int[] slots = slotStackMap.keys();
        int i = 0;
        int remainder = size;
        Map<Integer, ItemStack> newStacks = new HashMap<>();
        while (i < slots.length && remainder > 0) {
            int slot = slots[i++];
            if (inv.canInsertItem(slot, null, externalStorage.getDirection().getOpposite())) {
                ItemStack slotStack = slotStackMap.get(slot);
                if (slotStack.getMaxStackSize() != slotStack.stackSize
                        && API.instance().getComparer().isEqualNoQuantity(slotStack, stack)) {
                    int canInsert = Math.min(slotStack.getMaxStackSize() - slotStack.stackSize, remainder);
                    remainder -= canInsert;
                    newStacks.put(slot, ItemHandlerHelper.copyStackWithSize(slotStack, slotStack.stackSize + canInsert));
                }
            }
        }
        if (remainder > 0) {
            int freeSlot = indexedInv.getFirstEmptySlot();
            while (remainder < 0 && freeSlot < indexedInv.getLastEmptySlot() && indexedInv.getStackInSlot(freeSlot++) == null) {
                if (inv.canInsertItem(freeSlot, null, externalStorage.getDirection().getOpposite())) {
                    int canInsert = Math.min(stack.getMaxStackSize(), remainder);
                    remainder -= canInsert;
                    newStacks.put(freeSlot, ItemHandlerHelper.copyStackWithSize(stack, canInsert));
                }
            }
        }
        if (!simulate) {
           for (Map.Entry<Integer, ItemStack> newStack : newStacks.entrySet()) {
               indexedInv.setInventorySlotContents(newStack.getKey(), newStack.getValue());
           }
           inv.markDirty();
        }
        return ItemHandlerHelper.copyStackWithSize(stack, remainder);
    }

    private ItemStack extractItem(InventoryTileEntity inv, IndexedInventory indexedInv, ItemStack stack, int size, int flags, boolean simulate) {
        TIntObjectMap<ItemStack> slotStackMap = indexedInv.getIndex().get(stack.getItem());
        int[] slots = slotStackMap.keys();
        int i = 0;
        int extracted = 0;
        ItemStack received = null;
        Map<Integer, ItemStack> newStacks = new HashMap<>();
        while (i < slots.length && extracted < size) {
            int slot = slots[i++];
            if (inv.canExtractItem(slot, null, externalStorage.getDirection().getOpposite())) {
                ItemStack slotStack = slotStackMap.get(slot);
                if (API.instance().getComparer().isEqual(slotStack, stack, flags)) {
                    int canExtract = Math.min(slotStack.stackSize, size - extracted);
                    extracted += canExtract;
                    if (received == null) {
                        received = ItemHandlerHelper.copyStackWithSize(slotStack, canExtract);
                    }
                    newStacks.put(slot, ItemHandlerHelper.copyStackWithSize(slotStack, slotStack.stackSize - canExtract));
                }
            }
        }
        if (!simulate) {
            if (newStacks.size() > 0) {
                for (Map.Entry<Integer, ItemStack> newStack : newStacks.entrySet()) {
                    indexedInv.setInventorySlotContents(newStack.getKey(), newStack.getValue());
                }
                inv.markDirty();
            }
        }
        return received == null ? null : ItemHandlerHelper.copyStackWithSize(received, extracted);
    }

    public static boolean isValid(TileEntity facing) {
        return facing instanceof InventoryTileEntity;
    }
}
