package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.IItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.container.slot.SlotUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileWirelessTransmitter;

public class ContainerWirelessTransmitter extends ContainerBase {
    public ContainerWirelessTransmitter(EntityPlayer player, TileWirelessTransmitter wirelessTransmitter) {
        super(player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(wirelessTransmitter, i, 187, 6 + (i * 18), new SlotUpgrade(ItemUpgrade.TYPE_RANGE)));
        }

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 8) {
                if (!mergeItemStack(stack, 9, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 9, false)) {
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
