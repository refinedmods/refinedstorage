package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilterItemOrFluid;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDiskDrive extends ContainerBase {
    public ContainerDiskDrive(TileDiskDrive drive, EntityPlayer player) {
        super(drive, player);

        int x = 80;
        int y = 54;

        for (int i = 0; i < 8; ++i) {
            addSlotToContainer(new SlotItemHandler(drive.getNode().getDisks(), i, x + ((i % 2) * 18), y + Math.floorDiv(i, 2) * 18));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterItemOrFluid(drive.getNode(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 8) {
                if (!mergeItemStack(stack, 8 + 9, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 8, false)) {
                return mergeItemStackToFilters(stack, 8, 8 + 9);
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}
