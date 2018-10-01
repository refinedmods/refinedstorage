package com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.NetworkItemAction;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementError;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreviewResponse;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingStartResponse;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

public class ItemGridHandler implements IItemGridHandler {
    private INetwork network;

    public ItemGridHandler(INetwork network) {
        this.network = network;
    }

    @Override
    public void onExtract(EntityPlayerMP player, int hash, int flags) {
        ItemStack item = network.getItemStorageCache().getList().get(hash);

        if (item == null || !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
            return;
        }

        int itemSize = item.getCount();
        // We copy here because some mods change the NBT tag of an item after getting the stack limit
        int maxItemSize = item.getItem().getItemStackLimit(item.copy());

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

        if (took != null) {
            if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
                IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

                if (ItemHandlerHelper.insertItem(playerInventory, took, true).isEmpty()) {
                    took = network.extractItem(item, size, Action.PERFORM);

                    ItemHandlerHelper.insertItem(playerInventory, took, false);
                }
            } else {
                took = network.extractItem(item, size, Action.PERFORM);

                if (single && !held.isEmpty()) {
                    held.grow(1);
                } else {
                    player.inventory.setItemStack(took);
                }

                player.updateHeldItem();
            }

            INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

            if (networkItem != null) {
                networkItem.onAction(NetworkItemAction.ITEM_EXTRACTED);
            }
        }
    }

    @Override
    public ItemStack onInsert(EntityPlayerMP player, ItemStack stack) {
        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return stack;
        }

        network.getItemStorageTracker().changed(player, stack.copy());

        ItemStack remainder = network.insertItem(stack, stack.getCount(), Action.PERFORM);

        INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

        if (networkItem != null) {
            networkItem.onAction(NetworkItemAction.ITEM_INSERTED);
        }

        return remainder;
    }

    @Override
    public void onInsertHeldItem(EntityPlayerMP player, boolean single) {
        if (player.inventory.getItemStack().isEmpty() || !network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return;
        }

        ItemStack stack = player.inventory.getItemStack();
        int size = single ? 1 : stack.getCount();

        network.getItemStorageTracker().changed(player, stack.copy());

        if (single) {
            if (network.insertItem(stack, size, Action.SIMULATE) == null) {
                network.insertItem(stack, size, Action.PERFORM);

                stack.shrink(size);

                if (stack.getCount() == 0) {
                    player.inventory.setItemStack(ItemStack.EMPTY);
                }
            }
        } else {
            player.inventory.setItemStack(StackUtils.nullToEmpty(network.insertItem(stack, size, Action.PERFORM)));
        }

        player.updateHeldItem();

        INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

        if (networkItem != null) {
            networkItem.onAction(NetworkItemAction.ITEM_INSERTED);
        }
    }

    @Override
    public ItemStack onShiftClick(EntityPlayerMP player, ItemStack stack) {
        return StackUtils.nullToEmpty(onInsert(player, stack));
    }

    @Override
    public void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity, boolean noPreview) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        IStackList<ItemStack> cache = API.instance().createItemStackList();

        for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                cache.add(output);
            }
        }

        ItemStack stack = cache.get(hash);

        if (stack != null) {
            Thread calculationThread = new Thread(() -> {
                ICraftingTask task = network.getCraftingManager().create(stack, quantity);
                if (task == null) {
                    return;
                }

                ICraftingTaskError error = task.calculate();

                if (error != null) {
                    RS.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(Collections.singletonList(new CraftingPreviewElementError(error.getType(), error.getRecursedPattern() == null ? ItemStack.EMPTY : error.getRecursedPattern().getStack())), hash, quantity, false), player);
                } else if (noPreview && !task.hasMissing()) {
                    network.getCraftingManager().add(task);

                    RS.INSTANCE.network.sendTo(new MessageGridCraftingStartResponse(), player);
                } else {
                    RS.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(task.getPreviewStacks(), hash, quantity, false), player);
                }
            }, "RS crafting preview calculation");

            calculationThread.start();
        }
    }

    @Override
    public void onCraftingRequested(EntityPlayerMP player, int hash, int quantity) {
        if (quantity <= 0 || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        ItemStack stack = null;

        for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                if (API.instance().getItemStackHashCode(output) == hash) {
                    stack = output;

                    break;
                }
            }

            if (stack != null) {
                break;
            }
        }

        if (stack != null) {
            ICraftingTask task = network.getCraftingManager().create(stack, quantity);
            if (task == null) {
                return;
            }

            ICraftingTaskError error = task.calculate();
            if (error == null && !task.hasMissing()) {
                network.getCraftingManager().add(task);
            }
        }
    }

    @Override
    public void onCraftingCancelRequested(EntityPlayerMP player, @Nullable UUID id) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        network.getCraftingManager().cancel(id);

        INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

        if (networkItem != null) {
            networkItem.onAction(id == null ? NetworkItemAction.CRAFTING_TASK_ALL_CANCELLED : NetworkItemAction.CRAFTING_TASK_CANCELLED);
        }
    }
}
