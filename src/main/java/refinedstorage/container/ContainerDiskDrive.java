package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.container.slot.BasicItemValidator;
import refinedstorage.container.slot.SlotFiltered;
import refinedstorage.tile.TileDiskDrive;

public class ContainerDiskDrive extends ContainerStorage {
    public ContainerDiskDrive(EntityPlayer player, TileDiskDrive drive) {
        super(player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(drive, i, 98 + (i * 18), 78, new BasicItemValidator(RefinedStorageItems.STORAGE_DISK)));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotFiltered(drive, 4 + i, 98 + (i * 18), 96, new BasicItemValidator(RefinedStorageItems.STORAGE_DISK)));
        }

        addSpecimenAndPlayerInventorySlots(drive.getInventory());
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack().copy();

            if (index < 8) {
                if (!mergeItemStack(stack, 8, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 8, false)) {
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
