package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class ItemGridHandlerPortable implements IItemGridHandler {
    private IPortableGrid portableGrid;
    private IGrid grid;

    public ItemGridHandlerPortable(IPortableGrid portableGrid, IGrid grid) {
        this.portableGrid = portableGrid;
        this.grid = grid;
    }

    @Override
    public void onExtract(EntityPlayerMP player, int hash, int flags) {
        if (portableGrid.getStorage() == null || !grid.isActive()) {
            return;
        }

        ItemStack item = portableGrid.getCache().getList().get(hash);

        if (item == null) {
            return;
        }

        int itemSize = item.getCount();
        int maxItemSize = item.getItem().getItemStackLimit(item);

        boolean single = (flags & EXTRACT_SINGLE) == EXTRACT_SINGLE;

        ItemStack held = player.inventory.getItemStack();

        if (single) {
            if (!held.isEmpty() && (!API.instance().getComparer().isEqualNoQuantity(item, held) || held.getCount() + 1 > held.getMaxStackSize())) {
                return;
            }
        } else if (!player.inventory.getItemStack().isEmpty()) {
            return;
        }

        int size = 64;

        if ((flags & EXTRACT_HALF) == EXTRACT_HALF && itemSize > 1) {
            size = itemSize / 2;

            if (size > maxItemSize / 2) {
                size = maxItemSize / 2;
            }
        } else if (single) {
            size = 1;
        } else if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
            // NO OP, the quantity already set (64) is needed for shift
        }

        size = Math.min(size, maxItemSize);

        ItemStack took = portableGrid.getStorage().extract(item, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, true);

        if (took != null) {
            if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
                IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

                if (ItemHandlerHelper.insertItem(playerInventory, took, true).isEmpty()) {
                    took = portableGrid.getStorage().extract(item, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, false);

                    ItemHandlerHelper.insertItem(playerInventory, took, false);
                }
            } else {
                took = portableGrid.getStorage().extract(item, size, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, false);

                if (single && !held.isEmpty()) {
                    held.grow(1);
                } else {
                    player.inventory.setItemStack(took);
                }

                player.updateHeldItem();
            }

            portableGrid.drainEnergy(RS.INSTANCE.config.portableGridExtractUsage);
        }
    }

    @Nullable
    @Override
    public ItemStack onInsert(EntityPlayerMP player, ItemStack stack) {
        if (portableGrid.getStorage() == null || !grid.isActive()) {
            return stack;
        }

        ItemStack remainder = portableGrid.getStorage().insert(stack, stack.getCount(), false);

        portableGrid.drainEnergy(RS.INSTANCE.config.portableGridInsertUsage);

        return remainder;
    }

    @Override
    public void onInsertHeldItem(EntityPlayerMP player, boolean single) {
        if (player.inventory.getItemStack().isEmpty() || portableGrid.getStorage() == null || !grid.isActive()) {
            return;
        }

        ItemStack stack = player.inventory.getItemStack();
        int size = single ? 1 : stack.getCount();

        if (single) {
            if (portableGrid.getStorage().insert(stack, size, true) == null) {
                portableGrid.getStorage().insert(stack, size, false);

                stack.shrink(size);

                if (stack.getCount() == 0) {
                    player.inventory.setItemStack(ItemStack.EMPTY);
                }
            }
        } else {
            player.inventory.setItemStack(RSUtils.transformNullToEmpty(portableGrid.getStorage().insert(stack, size, false)));
        }

        player.updateHeldItem();

        portableGrid.drainEnergy(RS.INSTANCE.config.portableGridInsertUsage);
    }

    @Override
    public ItemStack onShiftClick(EntityPlayerMP player, ItemStack stack) {
        return RSUtils.transformNullToEmpty(onInsert(player, stack));
    }

    @Override
    public void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity, boolean noPreview) {
        // NO OP
    }

    @Override
    public void onCraftingRequested(EntityPlayerMP player, ItemStack stack, int quantity) {
        // NO OP
    }

    @Override
    public void onCraftingCancelRequested(EntityPlayerMP player, int id) {
        // NO OP
    }
}
