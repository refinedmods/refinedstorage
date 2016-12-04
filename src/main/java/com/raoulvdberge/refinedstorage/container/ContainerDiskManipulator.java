package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotBase;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterType;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDiskManipulator extends ContainerBase {
    public ContainerDiskManipulator(TileDiskManipulator manipulator, EntityPlayer player) {
        super(manipulator, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotBase(manipulator.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotBase(manipulator.getInputDisks(), i, 44, 57 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotBase(manipulator.getOutputDisks(), i, 116, 57 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterType(manipulator, i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 129);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4 + 6) {
                if (!mergeItemStack(stack, 4 + 6 + 9, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 4 + 3, false)) {
                return mergeItemStackToSpecimen(stack, 4 + 6, 4 + 6 + 9);
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
