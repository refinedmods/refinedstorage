package refinedstorage.tile.controller;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.NetworkMaster;
import refinedstorage.autocrafting.CraftingPattern;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.GridPullFlags;

public class StorageHandler {
    public static final int MAX_CRAFTING_PER_REQUEST = 500;

    private NetworkMaster network;

    public StorageHandler(NetworkMaster network) {
        this.network = network;
    }

    public void onPull(ItemStack stack, int flags, EntityPlayerMP player) {
        if (player.inventory.getItemStack() != null) {
            return;
        }

        int size = 64;

        if (GridPullFlags.isPullingHalf(flags) && stack.stackSize > 1) {
            size = stack.stackSize / 2;

            if (size > 32) {
                size = 32;
            }
        } else if (GridPullFlags.isPullingOne(flags)) {
            size = 1;
        } else if (GridPullFlags.isPullingWithShift(flags)) {
            // NO OP, the quantity already set (64) is needed for shift
        }

        size = Math.min(size, stack.getItem().getItemStackLimit(stack));

        ItemStack took = network.take(stack, size);

        if (took != null) {
            if (GridPullFlags.isPullingWithShift(flags)) {
                if (!player.inventory.addItemStackToInventory(took.copy())) {
                    InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), took);
                }
            } else {
                player.inventory.setItemStack(took);
                player.updateHeldItem();
            }

            network.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PULL);
        }
    }

    public ItemStack onPush(ItemStack stack) {
        return network.push(stack, stack.stackSize, false);
    }

    public void onHeldItemPush(boolean single, EntityPlayerMP player) {
        if (player.inventory.getItemStack() == null) {
            return;
        }

        ItemStack stack = player.inventory.getItemStack();
        int size = single ? 1 : stack.stackSize;

        if (single) {
            if (network.push(stack, size, true) == null) {
                network.push(stack, size, false);

                stack.stackSize -= size;

                if (stack.stackSize == 0) {
                    player.inventory.setItemStack(null);
                }
            }
        } else {
            player.inventory.setItemStack(network.push(stack, size, false));
        }

        player.updateHeldItem();

        network.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PUSH);
    }

    public void onCraftingRequested(ItemStack stack, int quantity) {
        if (quantity <= 0 || quantity > MAX_CRAFTING_PER_REQUEST) {
            return;
        }

        int quantityPerRequest = 0;

        System.out.println("stack:"+stack);
        CraftingPattern pattern = network.getPatternWithBestScore(stack);
        System.out.println("pattern: " +pattern);

        if (pattern != null) {
            for (ItemStack output : pattern.getOutputs()) {
                System.out.println("output:"+output);
                if (RefinedStorageUtils.compareStackNoQuantity(stack, output)) {
                    quantityPerRequest += output.stackSize;
                    System.out.println("QPR:"+quantityPerRequest);

                    if (!pattern.isProcessing()) {
                        break;
                    }
                }
            }

            while (quantity > 0) {
                network.addCraftingTaskAsLast(network.createCraftingTask(pattern));

                quantity -= quantityPerRequest;
            }
        }
    }

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
