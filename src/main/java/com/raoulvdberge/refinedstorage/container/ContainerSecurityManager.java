package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSecurityManager extends ContainerBase {
    public ContainerSecurityManager(TileSecurityManager tile, EntityPlayer player) {
        super(tile, player);

        int x = 8;
        int y = 20;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getCards(), i, x, y));

            if (((i + 1) % 9) == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlotToContainer(new SlotItemHandler(tile.getEditCard(), 0, 80, 70));

        addPlayerInventory(8, 127);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < (9 * 2) + 1) {
                if (!mergeItemStack(stack, (9 * 2) + 1, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 9 * 2, false)) {
                return ItemStack.EMPTY;
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
