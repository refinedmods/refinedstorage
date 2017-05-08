package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreviewResponse;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingStartResponse;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemGridHandler implements IItemGridHandler {
    private INetworkMaster network;

    public ItemGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void onExtract(EntityPlayerMP player, int hash, int flags) {
        ItemStack item = network.getItemStorageCache().getList().get(hash);

        if (item == null || !network.getSecurityManager().hasPermission(Permission.EXTRACT, player)) {
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

        ItemStack took = network.extractItem(item, size, true);

        if (took != null) {
            if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
                IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

                if (ItemHandlerHelper.insertItem(playerInventory, took, true).isEmpty()) {
                    took = network.extractItem(item, size, false);

                    ItemHandlerHelper.insertItem(playerInventory, took, false);
                }
            } else {
                took = network.extractItem(item, size, false);

                if (single && !held.isEmpty()) {
                    held.grow(1);
                } else {
                    player.inventory.setItemStack(took);
                }

                player.updateHeldItem();
            }

            INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

            if (networkItem != null && networkItem instanceof NetworkItemWirelessGrid) {
                ((NetworkItemWirelessGrid) networkItem).drainEnergy(RS.INSTANCE.config.wirelessGridExtractUsage);
            }
        }
    }

    @Override
    public ItemStack onInsert(EntityPlayerMP player, ItemStack stack) {
        if (!network.getSecurityManager().hasPermission(Permission.INSERT, player)) {
            return stack;
        }

        ItemStack remainder = network.insertItem(stack, stack.getCount(), false);

        INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

        if (networkItem != null && networkItem instanceof NetworkItemWirelessGrid) {
            ((NetworkItemWirelessGrid) networkItem).drainEnergy(RS.INSTANCE.config.wirelessGridInsertUsage);
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

        if (single) {
            if (network.insertItem(stack, size, true) == null) {
                network.insertItem(stack, size, false);

                stack.shrink(size);

                if (stack.getCount() == 0) {
                    player.inventory.setItemStack(ItemStack.EMPTY);
                }
            }
        } else {
            player.inventory.setItemStack(RSUtils.transformNullToEmpty(network.insertItem(stack, size, false)));
        }

        player.updateHeldItem();

        INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

        if (networkItem != null && networkItem instanceof NetworkItemWirelessGrid) {
            ((NetworkItemWirelessGrid) networkItem).drainEnergy(RS.INSTANCE.config.wirelessGridInsertUsage);
        }
    }

    @Override
    public ItemStack onShiftClick(EntityPlayerMP player, ItemStack stack) {
        return RSUtils.transformNullToEmpty(onInsert(player, stack));
    }

    @Override
    public void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity, boolean noPreview) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        IStackList<ItemStack> cache = API.instance().createItemStackList();

        for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                if (output != null) {
                    cache.add(output);
                }
            }
        }

        ItemStack stack = cache.get(hash);

        if (stack != null) {
            Thread calculationThread = new Thread(() -> {
                ICraftingTask task = new CraftingTask(network, stack, network.getCraftingManager().getPatternChain(stack), quantity, false);

                task.calculate();

                if (noPreview && task.getMissing().isEmpty()) {
                    network.getCraftingManager().add(task);

                    RS.INSTANCE.network.sendTo(new MessageGridCraftingStartResponse(), player);
                } else {
                    RS.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(task.getPreviewStacks(), stack, quantity), player);
                }
            }, "RS crafting calculation");

            calculationThread.start();
        }
    }

    @Override
    public void onCraftingRequested(EntityPlayerMP player, ItemStack stack, int quantity) {
        if (quantity <= 0 || !network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        if (stack != null) {
            ICraftingTask task = new CraftingTask(network, stack, network.getCraftingManager().getPatternChain(stack), quantity, false);

            task.calculate();

            task.getMissing().clear();

            network.getCraftingManager().add(task);
        }
    }

    @Override
    public void onCraftingCancelRequested(EntityPlayerMP player, int id) {
        if (!network.getSecurityManager().hasPermission(Permission.AUTOCRAFTING, player)) {
            return;
        }

        ICraftingManager manager = network.getCraftingManager();

        if (id >= 0 && id < manager.getTasks().size()) {
            manager.cancel(manager.getTasks().get(id));
        } else if (id == -1) {
            for (ICraftingTask task : manager.getTasks()) {
                manager.cancel(task);
            }
        }

        INetworkItem networkItem = network.getNetworkItemHandler().getItem(player);

        if (networkItem != null && networkItem instanceof NetworkItemWirelessCraftingMonitor) {
            ((NetworkItemWirelessCraftingMonitor) networkItem).drainEnergy(id == -1 ? RS.INSTANCE.config.wirelessCraftingMonitorCancelAllUsage : RS.INSTANCE.config.wirelessCraftingMonitorCancelUsage);
        }
    }
}
