package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ServerProxy {
    public static void onInventoryScrollMessage(ServerPlayerEntity player, int slot, boolean shift, boolean up) {
        if (player == null || !(player.openContainer instanceof GridContainer)) {
            return;
        }

        IGrid grid = ((GridContainer) player.openContainer).getGrid();
        if (grid.getItemHandler() == null) {
            return;
        }

        IItemGridHandler itemHandler = grid.getItemHandler();
        int flags = ItemGridHandler.EXTRACT_SINGLE;
        ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
        ItemStack stackOnCursor = player.inventory.getItemStack();

        if (shift) { // shift
            flags |= ItemGridHandler.EXTRACT_SHIFT;
            if (up) { // scroll up
                player.inventory.setInventorySlotContents(slot, itemHandler.onInsert(player, stackInSlot, true));
            } else { // scroll down
                itemHandler.onExtract(player, stackInSlot, slot, flags);
            }

        } else { //ctrl
            if (up) { // scroll up
                itemHandler.onInsert(player, stackOnCursor, true);
                player.updateHeldItem();
            } else { //scroll down
                if (stackOnCursor.isEmpty()) {
                    itemHandler.onExtract(player, stackInSlot, -1, flags);
                } else {
                    itemHandler.onExtract(player, stackOnCursor, -1, flags);
                }
            }
        }
    }

    public static void onGridScrollMessage(ServerPlayerEntity player, UUID id, boolean shift, boolean ctrl, boolean up) {
        if (player == null || !(player.openContainer instanceof GridContainer)) {
            return;
        }

        IGrid grid = ((GridContainer) player.openContainer).getGrid();
        if (grid.getItemHandler() == null) {
            return;
        }

        int flags = ItemGridHandler.EXTRACT_SINGLE;

        if (!id.equals(new UUID(0, 0))) { //isOverStack
            if (shift && !ctrl) { //shift
                flags |= ItemGridHandler.EXTRACT_SHIFT;

                if (up) { //scroll up, insert hovering stack pulled from Inventory
                    ItemStorageCache cache = (ItemStorageCache) grid.getStorageCache();
                    if (cache == null) {
                        return;
                    }

                    ItemStack stack = cache.getList().get(id);
                    if (stack == null) {
                        return;
                    }

                    int slot = player.inventory.getSlotFor(stack);
                    if (slot != -1) {
                        grid.getItemHandler().onInsert(player, player.inventory.getStackInSlot(slot), true);
                        return;
                    }

                } else { //scroll down, extract hovering item
                    grid.getItemHandler().onExtract(player, id, -1, flags);
                    return;
                }

            } else if (!shift && ctrl) { //ctrl
                if (!up) { //scroll down, extract hovering item
                    grid.getItemHandler().onExtract(player, id, -1, flags);
                    return;
                }
            }
        }

        if (up) { //scroll up, insert item from cursor
            grid.getItemHandler().onInsert(player, player.inventory.getItemStack(), true);
            player.updateHeldItem();
        }
    }
}
