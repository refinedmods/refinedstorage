package refinedstorage.tile.controller;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.GridPullFlags;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.autocrafting.task.ICraftingTask;

public class StorageHandler {
    public static final int MAX_CRAFTING_PER_REQUEST = 500;

    private TileController controller;

    public StorageHandler(TileController controller) {
        this.controller = controller;
    }

    public void handlePull(int id, int flags, EntityPlayerMP player) {
        if (player.inventory.getItemStack() != null) {
            return;
        }

        if (id < 0 || id > controller.getItemGroups().size() - 1) {
            return;
        }

        ItemGroup group = controller.getItemGroups().get(id);

        int quantity = 64;

        if (GridPullFlags.isPullingHalf(flags) && group.getQuantity() > 1) {
            quantity = group.getQuantity() / 2;

            if (quantity > 32) {
                quantity = 32;
            }
        } else if (GridPullFlags.isPullingOne(flags)) {
            quantity = 1;
        } else if (GridPullFlags.isPullingWithShift(flags)) {
            // NO OP, the quantity already set (64) is needed for shift
        }

        if (quantity > group.getType().getItemStackLimit(group.toStack())) {
            quantity = group.getType().getItemStackLimit(group.toStack());
        }

        ItemStack took = controller.take(group.copy(quantity).toStack());

        if (took != null) {
            if (GridPullFlags.isPullingWithShift(flags)) {
                if (!player.inventory.addItemStackToInventory(took.copy())) {
                    controller.push(took);
                }
            } else {
                player.inventory.setItemStack(took);
                player.updateHeldItem();
            }

            controller.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PULL);
        }
    }

    public void handlePush(int playerSlot, boolean one, EntityPlayerMP player) {
        ItemStack stack;

        if (playerSlot == -1) {
            stack = player.inventory.getItemStack().copy();

            if (one) {
                stack.stackSize = 1;
            }
        } else {
            stack = player.inventory.getStackInSlot(playerSlot);
        }

        if (stack != null) {
            boolean success = controller.push(stack);

            if (success) {
                if (playerSlot == -1) {
                    if (one) {
                        player.inventory.getItemStack().stackSize--;

                        if (player.inventory.getItemStack().stackSize == 0) {
                            player.inventory.setItemStack(null);
                        }
                    } else {
                        player.inventory.setItemStack(null);
                    }

                    player.updateHeldItem();
                } else {
                    player.inventory.setInventorySlotContents(playerSlot, null);
                }
            }

            controller.getWirelessGridHandler().drainEnergy(player, ItemWirelessGrid.USAGE_PUSH);
        }
    }

    public void handleCraftingRequest(int id, int quantity) {
        if (id >= 0 && id < controller.getItemGroups().size() && quantity > 0 && quantity <= MAX_CRAFTING_PER_REQUEST) {
            ItemStack requested = controller.getItemGroups().get(id).toStack();
            int quantityPerRequest = 0;
            CraftingPattern pattern = controller.getPattern(requested);

            if (pattern != null) {
                for (ItemStack output : pattern.getOutputs()) {
                    if (RefinedStorageUtils.compareStackNoQuantity(requested, output)) {
                        quantityPerRequest = output.stackSize;

                        break;
                    }
                }

                while (quantity > 0) {
                    controller.addCraftingTaskAsLast(controller.createCraftingTask(pattern));

                    quantity -= quantityPerRequest;
                }
            }
        }
    }

    public void handleCraftingCancel(int id) {
        if (id >= 0 && id < controller.getCraftingTasks().size()) {
            controller.cancelCraftingTask(controller.getCraftingTasks().get(id));
        } else if (id == -1) {
            for (ICraftingTask task : controller.getCraftingTasks()) {
                controller.cancelCraftingTask(task);
            }
        }
    }
}
