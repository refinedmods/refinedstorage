package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotOutput;
import com.raoulvdberge.refinedstorage.container.slot.SlotSpecimen;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerInterface extends ContainerBase {
    public ContainerInterface(TileInterface tile, EntityPlayer player) {
        super(tile, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getImportItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(tile.getExportSpecimenItems(), i, 8 + (18 * i), 54, SlotSpecimen.SPECIMEN_SIZE));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotOutput(tile.getExportItems(), i, 8 + (18 * i), 100));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 134);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 9) {
                if (!mergeItemStack(stack, 9 + 9 + 9 + 4, inventorySlots.size(), false)) {
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
