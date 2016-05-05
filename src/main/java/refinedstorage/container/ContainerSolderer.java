package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.IItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.container.slot.SlotUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileSolderer;

public class ContainerSolderer extends ContainerBase {
    public ContainerSolderer(EntityPlayer player, TileSolderer solderer) {
        super(player);

        int x = 44;
        int y = 20;

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new Slot(solderer, i, x, y));

            y += 18;
        }

        addSlotToContainer(new SlotOutput(solderer, 3, 134, 38));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(solderer.getUpgradesInventory(), i, 187, 6 + (i * 18), new SlotUpgrade(ItemUpgrade.TYPE_SPEED)));
        }

        addPlayerInventory(8, 95);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 3) {
                if (!mergeItemStack(stack, 3, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 3, false)) {
                return null;
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
