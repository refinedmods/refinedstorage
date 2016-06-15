package refinedstorage.tile.controller;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.autocrafting.CraftingPattern;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.GridPullFlags;

public class StorageHandler {
    public static final int MAX_CRAFTING_PER_REQUEST = 500;

    private TileController controller;

    public StorageHandler(TileController controller) {
        this.controller = controller;
    }

    public void onPull(int id, int flags, EntityPlayerMP player) {
        if (player.inventory.getItemStack() != null) {
            return;
        }

        if (id < 0 || id > controller.getItems().size() - 1) {
            return;
        }

        ItemStack stack = controller.getItems().get(id);

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

        ItemStack took = controller.take(stack, size);

        if (took != null) {
            if (GridPullFlags.isPullingWithShift(flags)) {
                if (!player.inventory.addItemStackToInventory(took.copy())) {
                    InventoryHelper.spawnItemStack(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), took);
                }
            } else {
                player.inventory.setItemStack(took);
                player.updateHeldItem();
            }

            controller.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PULL);
        }
    }

    public void onHeldItemPush(boolean one, EntityPlayerMP player) {
        if (player.inventory.getItemStack() == null) {
            return;
        }

        ItemStack stack = player.inventory.getItemStack();
        int size = one ? 1 : stack.stackSize;

        if (one) {
            if (controller.push(stack, size, true) == null) {
                controller.push(stack, size, false);

                stack.stackSize -= size;

                if (stack.stackSize == 0) {
                    player.inventory.setItemStack(null);
                }
            }
        } else {
            player.inventory.setItemStack(controller.push(stack, size, false));
        }

        player.updateHeldItem();

        controller.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PUSH);
    }

    public void onCraftingRequested(int id, int quantity) {
        if (id >= 0 && id < controller.getItems().size() && quantity > 0 && quantity <= MAX_CRAFTING_PER_REQUEST) {
            ItemStack requested = controller.getItems().get(id);

            int quantityPerRequest = 0;

            CraftingPattern pattern = controller.getPatternWithBestScore(requested);

            if (pattern != null) {
                for (ItemStack output : pattern.getOutputs()) {
                    if (RefinedStorageUtils.compareStackNoQuantity(requested, output)) {
                        quantityPerRequest += output.stackSize;

                        if (!pattern.isProcessing()) {
                            break;
                        }
                    }
                }

                while (quantity > 0) {
                    controller.addCraftingTaskAsLast(controller.createCraftingTask(pattern));

                    quantity -= quantityPerRequest;
                }
            }
        }
    }

    public void onCraftingCancelRequested(int id) {
        if (id >= 0 && id < controller.getCraftingTasks().size()) {
            controller.cancelCraftingTask(controller.getCraftingTasks().get(id));
        } else if (id == -1) {
            for (ICraftingTask task : controller.getCraftingTasks()) {
                controller.cancelCraftingTask(task);
            }
        }
    }
}
