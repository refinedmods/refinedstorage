package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.container.slot.SlotBase;
import com.raoulvdberge.refinedstorage.container.slot.SlotOutput;
import com.raoulvdberge.refinedstorage.tile.TileSolderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSolderer extends ContainerBase {
    public ContainerSolderer(TileSolderer solderer, EntityPlayer player) {
        super(solderer, player);

        int x = 44;
        int y = 20;

        for (int i = 0; i < 3; ++i) {
            addSlotToContainer(new SlotBase(solderer.getItems(), i, x, y));

            y += 18;
        }

        addSlotToContainer(new SlotOutput(solderer.getResult(), 0, 127, 38));

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotBase(solderer.getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 89);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4) {
                if (!mergeItemStack(stack, 4 + 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 4 + 4) {
                if (!mergeItemStack(stack, 4 + 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.getItem() != RSItems.UPGRADE || !mergeItemStack(stack, 4, 4 + 4, false)) {
                    if (!mergeItemStack(stack, 0, 3, false)) { // 0 - 3 because we can't shift click to output slot
                        return ItemStack.EMPTY;
                    }
                }
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
