package com.refinedmods.refinedstorage.apiimpl.network.grid.handler;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ErrorCraftingPreviewElement;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.network.grid.GridCraftingPreviewResponseMessage;
import com.refinedmods.refinedstorage.network.grid.GridCraftingStartResponseMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class ItemGridHandler implements IItemGridHandler {
    private final INetwork network;

    public ItemGridHandler(INetwork network) {
        this.network = network;
    }

    @Override
    public void onExtract(ServerPlayerEntity player, ItemStack stack, int preferredSlot, int flags) {
        StackListEntry<ItemStack> stackEntry = network.getItemStorageCache().getList().getEntry(stack, IComparer.COMPARE_NBT);
        if (stackEntry != null) {
            onExtract(player, stackEntry.getId(), preferredSlot, flags);
        }
    }

    @Override
    public void onExtract(ServerPlayerEntity player, UUID id, int preferredSlot, int flags) {
        ItemStack item = network.getItemStorageCache().getList().get(id);

        if (item == null || !network.getSecurityManager().hasPermission(Permission.EXTRACT, player) || !network.canRun()) {
            return;
        }

        int itemSize = item.getCount();
        // We copy here because some mods change the NBT tag of an item after getting the stack limit
        int maxItemSize = item.getItem().getItemStackLimit(item.copy());

        boolean single = (flags & EXTRACT_SINGLE) == EXTRACT_SINGLE;

        ItemStack held = player.inventory.getCarried();

        if (single) {
            if (!held.isEmpty() && (!API.instance().getComparer().isEqualNoQuantity(item, held) || held.getCount() + 1 > held.getMaxStackSize())) {
                return;
            }
        } else if (!player.inventory.getCarried().isEmpty()) {
            return;
        }

        int size = 64;

        if ((flags & EXTRACT_HALF) == EXTRACT_HALF && itemSize > 1) {
            size = itemSize / 2;

            // Rationale for this check:
            // If we have 32 buckets, and we want to extract half, we expect/need to get 8 (max stack size 16 / 2).
            // Without this check, we would get 16 (total stack size 32 / 2).
            // Max item size also can't be 1. Otherwise, if we want to extract half of 8 lava buckets, we would get size 0 (1 / 2).
            if (size > maxItemSize / 2 && maxItemSize != 1) {
                size = maxItemSize / 2;
            }
        } else if (single) {
            size = 1;
        } else if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
            // NO OP, the quantity already set (64) is needed for shift
        }

        size = Math.min(size, maxItemSize);

        // Do this before actually extracting, since external storage sends updates as soon as a change happens (so before the storage tracker used to track)
        network.getItemStorageTracker().changed(player, item.copy());

        ItemStack took = network.extractItem(item, size, Action.SIMULATE);

        if (!took.isEmpty()) {
            if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
                Optional<IItemHandler> playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).resolve();
                if (playerInventory.isPresent()) {
                    if (preferredSlot != -1) {
                        ItemStack remainder = playerInventory.get().insertItem(preferredSlot, took, true);
                        if (remainder.getCount() != took.getCount()) {
                            ItemStack inserted = network.extractItem(item, size - remainder.getCount(), Action.PERFORM);
                            playerInventory.get().insertItem(preferredSlot, inserted, false);
                            took.setCount(remainder.getCount());
                        }
                    }

                    if (!took.isEmpty() && ItemHandlerHelper.insertItemStacked(playerInventory.get(), took, true).isEmpty()) {
                        took = network.extractItem(item, size, Action.PERFORM);

                        ItemHandlerHelper.insertItemStacked(playerInventory.get(), took, false);
                    }
                }
            } else {
                took = network.extractItem(item, size, Action.PERFORM);

                if (!took.isEmpty()) {
                    if (single && !held.isEmpty()) {
                        held.grow(1);
                    } else {
                        player.inventory.setCarried(took);
                    }

                    player.broadcastCarriedItem();
                }
            }

            network.getNetworkItemManager().drainEnergy(player, RS.SERVER_CONFIG.getWirelessGrid().getExtractUsage());
        }
    }

    @Override
    @Nonnull
    public ItemStack onInsert(ServerPlayerEntity player, ItemStack stack, boolean single) {
        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player) || !network.canRun()) {
            return stack;
        }

        network.getItemStorageTracker().changed(player, stack.copy());

        ItemStack remainder;
        if (single) {
            if (network.insertItem(stack, 1, Action.SIMULATE).isEmpty()) {
                network.insertItem(stack, 1, Action.PERFORM);
                stack.shrink(1);
            }
            remainder = stack;
        } else {
            remainder = network.insertItem(stack, stack.getCount(), Action.PERFORM);
        }

        network.getNetworkItemManager().drainEnergy(player, RS.SERVER_CONFIG.getWirelessGrid().getInsertUsage());

        return remainder;
    }

    @Override
    public void onInsertHeldItem(ServerPlayerEntity player, boolean single) {
        if (player.inventory.getCarried().isEmpty() || !network.getSecurityManager().hasPermission(Permission.INSERT, player) || !network.canRun()) {
            return;
        }

        ItemStack stack = player.inventory.getCarried();
        int size = single ? 1 : stack.getCount();

        network.getItemStorageTracker().changed(player, stack.copy());

        if (single) {
            if (network.insertItem(stack, size, Action.SIMULATE).isEmpty()) {
                network.insertItem(stack, size, Action.PERFORM);

                stack.shrink(size);
            }
        } else {
            player.inventory.setCarried(network.insertItem(stack, size, Action.PERFORM));
        }

        player.broadcastCarriedItem();

        network.getNetworkItemManager().drainEnergy(player, RS.SERVER_CONFIG.getWirelessGrid().getInsertUsage());
    }

    @Override
    public void onCraftingPreviewRequested(ServerPlayerEntity player, UUID id, int quantity, boolean noPreview) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        ItemStack stack = network.getItemStorageCache().getCraftablesList().get(id);

        if (stack != null) {
            ICalculationResult result = network.getCraftingManager().create(stack, quantity);

            if (!result.isOk() && result.getType() != CalculationResultType.MISSING) {
                RS.NETWORK_HANDLER.sendTo(
                    player,
                    new GridCraftingPreviewResponseMessage(
                        Collections.singletonList(new ErrorCraftingPreviewElement(result.getType(), result.getRecursedPattern() == null ? ItemStack.EMPTY : result.getRecursedPattern().getStack())),
                        id,
                        quantity,
                        false
                    )
                );
            } else if (result.isOk() && noPreview) {
                network.getCraftingManager().start(result.getTask());

                RS.NETWORK_HANDLER.sendTo(player, new GridCraftingStartResponseMessage());
            } else {
                RS.NETWORK_HANDLER.sendTo(
                    player,
                    new GridCraftingPreviewResponseMessage(
                        result.getPreviewElements(),
                        id,
                        quantity,
                        false
                    )
                );
            }
        }
    }

    @Override
    public void onCraftingRequested(ServerPlayerEntity player, UUID id, int quantity) {
        if (quantity <= 0 || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        ItemStack stack = network.getItemStorageCache().getCraftablesList().get(id);

        if (stack != null) {
            ICalculationResult result = network.getCraftingManager().create(stack, quantity);
            if (result.isOk()) {
                network.getCraftingManager().start(result.getTask());
            }
        }
    }

    @Override
    public void onCraftingCancelRequested(ServerPlayerEntity player, @Nullable UUID id) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        network.getCraftingManager().cancel(id);

        network.getNetworkItemManager().drainEnergy(player, id == null ? RS.SERVER_CONFIG.getWirelessCraftingMonitor().getCancelAllUsage() : RS.SERVER_CONFIG.getWirelessCraftingMonitor().getCancelUsage());
    }

    @Override
    public void onInventoryScroll(ServerPlayerEntity player, int slot, boolean shift, boolean up) {
        onInventoryScroll(this, player, slot, shift, up, network);
    }

    public static void onInventoryScroll(IItemGridHandler gridHandler, ServerPlayerEntity player, int slot, boolean shift, boolean up, @Nullable INetwork network) {
        if (player == null || !(player.containerMenu instanceof GridContainer)) {
            return;
        }

        if (network != null && ((up && !network.getSecurityManager().hasPermission(Permission.INSERT, player)) || (!up && !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)))) {
            return;
        }

        int flags = EXTRACT_SINGLE;
        ItemStack stackInSlot = player.inventory.getItem(slot);
        ItemStack stackOnCursor = player.inventory.getCarried();

        if (shift) { // shift
            flags |= EXTRACT_SHIFT;

            if (!stackInSlot.isEmpty()) {
                if (up) { // scroll up
                    player.inventory.setItem(slot, gridHandler.onInsert(player, stackInSlot, true));
                } else { // scroll down
                    gridHandler.onExtract(player, stackInSlot, slot, flags);
                }
            }
        } else { //ctrl
            if (up) { // scroll up
                if (!stackOnCursor.isEmpty()) {
                    gridHandler.onInsert(player, stackOnCursor, true);
                    player.broadcastCarriedItem();
                }
            } else { //scroll down
                if (stackOnCursor.isEmpty()) {
                    gridHandler.onExtract(player, stackInSlot, -1, flags);
                } else {
                    gridHandler.onExtract(player, stackOnCursor, -1, flags);
                }
            }
        }
    }

    @Override
    public void onGridScroll(ServerPlayerEntity player, @Nullable UUID id, boolean shift, boolean up) {
        onGridScroll(this, player, id, shift, up, network);
    }

    public static void onGridScroll(IItemGridHandler gridHandler, ServerPlayerEntity player, @Nullable UUID id, boolean shift, boolean up, @Nullable INetwork network) {
        if (player == null || !(player.containerMenu instanceof GridContainer)) {
            return;
        }

        if (network != null && ((up && !network.getSecurityManager().hasPermission(Permission.INSERT, player)) || (!up && !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)))) {
            return;
        }

        IGrid grid = ((GridContainer) player.containerMenu).getGrid();

        int flags = EXTRACT_SINGLE;

        if (shift && id != null) {
            flags |= EXTRACT_SHIFT;
            if (up) { //scroll up, insert hovering stack pulled from Inventory
                IStorageCache<ItemStack> cache = grid.getStorageCache();
                if (cache == null || cache.getList().get(id) == null) {
                    return;
                }

                for (int i = 0; i < player.inventory.items.size(); i++) {
                    if (API.instance().getComparer().isEqual(player.inventory.getItem(i), cache.getList().get(id), IComparer.COMPARE_NBT)) {
                        gridHandler.onInsert(player, player.inventory.getItem(i), true);
                        break;
                    }
                }

            } else { //scroll down, extract hovering item
                gridHandler.onExtract(player, id, -1, flags);
            }
        } else { //ctrl
            if (!up && id != null) { //scroll down, extract hovering item
                gridHandler.onExtract(player, id, -1, flags);

            } else if (up && !player.inventory.getCarried().isEmpty()) { // insert stack from cursor
                gridHandler.onInsert(player, player.inventory.getCarried(), true);
                player.broadcastCarriedItem();
            }
        }
    }
}
