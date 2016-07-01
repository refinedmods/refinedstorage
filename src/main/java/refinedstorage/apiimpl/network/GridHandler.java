package refinedstorage.apiimpl.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.GridPullFlags;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.item.ItemWirelessGrid;

public class GridHandler implements IGridHandler {
    public static final int MAX_CRAFTING_PER_REQUEST = 500;

    private INetworkMaster network;

    public GridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void onPull(ItemStack stack, int flags, EntityPlayerMP player) {
        boolean single = (flags & GridPullFlags.PULL_SINGLE) == GridPullFlags.PULL_SINGLE;

        ItemStack held = player.inventory.getItemStack();

        if (single) {
            if (held != null && (!RefinedStorageUtils.compareStackNoQuantity(stack, held) || held.stackSize + 1 > held.getMaxStackSize())) {
                return;
            }
        } else if (player.inventory.getItemStack() != null) {
            return;
        }

        int size = 64;

        if ((flags & GridPullFlags.PULL_HALF) == GridPullFlags.PULL_HALF && stack.stackSize > 1) {
            size = stack.stackSize / 2;

            if (size > 32) {
                size = 32;
            }
        } else if (single) {
            size = 1;
        } else if ((flags & GridPullFlags.PULL_SHIFT) == GridPullFlags.PULL_SHIFT) {
            // NO OP, the quantity already set (64) is needed for shift
        }

        size = Math.min(size, stack.getItem().getItemStackLimit(stack));

        ItemStack took = RefinedStorageUtils.takeFromNetwork(network, stack, size);

        if (took != null) {
            if ((flags & GridPullFlags.PULL_SHIFT) == GridPullFlags.PULL_SHIFT) {
                if (!player.inventory.addItemStackToInventory(took.copy())) {
                    InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), took);
                }
            } else {
                if (held != null) {
                    held.stackSize++;
                } else {
                    player.inventory.setItemStack(took);
                }

                player.updateHeldItem();
            }

            network.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PULL);
        }
    }

    @Override
    public ItemStack onPush(ItemStack stack) {
        return network.push(stack, stack.stackSize, false);
    }

    @Override
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

    @Override
    public void onCraftingRequested(ItemStack stack, int quantity) {
        if (quantity <= 0 || quantity > MAX_CRAFTING_PER_REQUEST) {
            return;
        }

        int quantityPerRequest = 0;

        ICraftingPattern pattern = RefinedStorageUtils.getPatternFromNetwork(network, stack);

        if (pattern != null) {
            for (ItemStack output : pattern.getOutputs()) {
                if (RefinedStorageUtils.compareStackNoQuantity(stack, output)) {
                    quantityPerRequest += output.stackSize;

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
