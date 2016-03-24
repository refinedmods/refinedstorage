package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.container.slot.SlotOutput;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.tile.TileInterface;

public class ContainerInterface extends ContainerBase {
    public ContainerInterface(EntityPlayer player, TileInterface tile) {
        super(player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(tile, i, 8 + (18 * i), 20));
        }

        for (int i = 9; i < 18; ++i) {
            addSlotToContainer(new SlotSpecimen(tile, i, 8 + (18 * (i - 9)), 54, true));
        }

        for (int i = 18; i < 27; ++i) {
            addSlotToContainer(new SlotOutput(tile, i, 8 + (18 * (i - 18)), 100));
        }

        addPlayerInventory(8, 136);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 9) {
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
