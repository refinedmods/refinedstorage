package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerNetworkTransmitter extends ContainerBase {
    public ContainerNetworkTransmitter(TileNetworkTransmitter networkTransmitter, EntityPlayer player) {
        super(networkTransmitter, player);

        addSlotToContainer(new SlotItemHandler(networkTransmitter.getNetworkCard(), 0, 8, 20));

        addSlotToContainer(new SlotItemHandler(networkTransmitter.getUpgrades(), 0, 187, 6));

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index <= 1) {
                if (!mergeItemStack(stack, 2, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 2, false)) {
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
