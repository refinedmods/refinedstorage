package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.tile.TileSolderer;

public class ContainerSolderer extends ContainerBase {
    public ContainerSolderer(TileSolderer solderer, EntityPlayer player) {
        super(solderer, player);

        int x = 44;
        int y = 20;

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotItemHandler(solderer.getItems(), i, x, y));

            y += 18;
        }

        addSlotToContainer(new SlotOutput(solderer.getItems(), 3, 127, 38));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(solderer.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 89);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4) {
                if (!mergeItemStack(stack, 4 + 4, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (index < 4 + 4) {
                if (!mergeItemStack(stack, 4 + 4, inventorySlots.size(), false)) {
                    return null;
                }
            } else {
                if (stack.getItem() != RefinedStorageItems.UPGRADE || !mergeItemStack(stack, 4, 4 + 4, false)) {
                    if (!mergeItemStack(stack, 0, 3, false)) { // 0 - 3 because we can't shift click to output slot
                        return null;
                    }
                }
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}
