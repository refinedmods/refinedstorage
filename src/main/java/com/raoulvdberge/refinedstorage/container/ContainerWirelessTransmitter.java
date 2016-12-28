package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeWirelessTransmitter;
import com.raoulvdberge.refinedstorage.tile.TileWirelessTransmitter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerWirelessTransmitter extends ContainerBase {
    public ContainerWirelessTransmitter(TileWirelessTransmitter wirelessTransmitter, EntityPlayer player) {
        super(wirelessTransmitter, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(wirelessTransmitter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 4) {
                if (!mergeItemStack(stack, 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 4, false)) {
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
