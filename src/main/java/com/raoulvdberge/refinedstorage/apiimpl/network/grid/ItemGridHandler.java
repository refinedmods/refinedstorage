package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.CraftingTask;
import com.raoulvdberge.refinedstorage.network.MessageGridCraftingPreviewResponse;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemGridHandler implements IItemGridHandler {
    private INetworkMaster network;

    public ItemGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void onExtract(int hash, int flags, EntityPlayerMP player) {
        ItemStack item = network.getItemStorageCache().getList().get(hash);

        if (item == null) {
            return;
        }

        int itemSize = item.stackSize;
        int maxItemSize = item.getItem().getItemStackLimit(item);

        boolean single = (flags & EXTRACT_SINGLE) == EXTRACT_SINGLE;

        ItemStack held = player.inventory.getItemStack();

        if (single) {
            if (held != null && (!API.instance().getComparer().isEqualNoQuantity(item, held) || held.stackSize + 1 > held.getMaxStackSize())) {
                return;
            }
        } else if (player.inventory.getItemStack() != null) {
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

        ItemStack took = network.extractItem(item, size);

        if (took != null) {
            if ((flags & EXTRACT_SHIFT) == EXTRACT_SHIFT) {
                ItemStack remainder = ItemHandlerHelper.insertItem(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), took, false);

                if (remainder != null) {
                    network.insertItem(remainder, remainder.stackSize, false);
                }
            } else {
                if (single && held != null) {
                    held.stackSize++;
                } else {
                    player.inventory.setItemStack(took);
                }

                player.updateHeldItem();
            }

            network.getWirelessGridHandler().drainEnergy(player, RS.INSTANCE.config.wirelessGridExtractUsage);
        }
    }

    @Override
    public ItemStack onInsert(EntityPlayerMP player, ItemStack stack) {
        ItemStack remainder = network.insertItem(stack, stack.stackSize, false);

        network.getWirelessGridHandler().drainEnergy(player, RS.INSTANCE.config.wirelessGridInsertUsage);

        return remainder;
    }

    @Override
    public void onInsertHeldItem(EntityPlayerMP player, boolean single) {
        if (player.inventory.getItemStack() == null) {
            return;
        }

        ItemStack stack = player.inventory.getItemStack();
        int size = single ? 1 : stack.stackSize;

        if (single) {
            if (network.insertItem(stack, size, true) == null) {
                network.insertItem(stack, size, false);

                stack.stackSize -= size;

                if (stack.stackSize == 0) {
                    player.inventory.setItemStack(null);
                }
            }
        } else {
            player.inventory.setItemStack(network.insertItem(stack, size, false));
        }

        player.updateHeldItem();

        network.getWirelessGridHandler().drainEnergy(player, RS.INSTANCE.config.wirelessGridInsertUsage);
    }

    @Override
    public void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity) {
        ItemStack stack = network.getItemStorageCache().getList().get(hash);

        if (stack != null) {
            Thread calculationThread = new Thread(() -> {
                ICraftingTask task = new CraftingTask(network, stack, network.getPattern(stack), quantity);

                task.calculate();

                RS.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(task.getPreviewStacks(), hash, quantity), player);
            }, "RS crafting calculation");

            calculationThread.start();
        }
    }

    @Override
    public void onCraftingRequested(int hash, int quantity) {
        if (quantity <= 0) {
            return;
        }

        ItemStack stack = network.getItemStorageCache().getList().get(hash);

        if (stack != null) {
            ICraftingTask task = new CraftingTask(network, stack, network.getPattern(stack), quantity);

            task.calculate();
            task.getMissing().clear();

            network.addCraftingTask(task);
        }
    }

    @Override
    public void onCraftingCancelRequested(int id) {
        if (id >= 0 && id < network.getCraftingTasks().size()) {
            network.cancelCraftingTask(network.getCraftingTasks().get(id));
        } else if (id == -1) {
            for (ICraftingTask task : network.getCraftingTasks()) {
                network.cancelCraftingTask(task);
            }
        }
    }
}
