package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.container.slot.SlotSpecimenType;
import refinedstorage.tile.TileDiskManipulator;

public class ContainerDiskManipulator extends ContainerBase {
    public ContainerDiskManipulator(TileDiskManipulator manipulator, EntityPlayer player) {
        super(manipulator, player);

        for (int i = 0; i < 6; ++i) {
            addSlotToContainer(new SlotItemHandler(manipulator.getDisks(), i, 26 + (i % 2 * 18), ((i / 2) * 18) + 57));
        }

        for (int i = 0; i < 6; ++i) {
            addSlotToContainer(new SlotItemHandler(manipulator.getDisks(), 6 + i, 116 + (i%2 * 18), ((i / 2) * 18) + 57));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimenType(manipulator, i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 129);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 12) {
                if (!mergeItemStack(stack, 12 + 9, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 12, false)) {
                return mergeItemStackToSpecimen(stack, 12, 12 + 9);
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
